package com.diamssword.greenresurgence.entities;

import com.diamssword.greenresurgence.MEntities;
import com.diamssword.greenresurgence.containers.GenericContainer;
import com.diamssword.greenresurgence.containers.GridContainer;
import com.diamssword.greenresurgence.items.AbstractBackpackItem;
import com.diamssword.greenresurgence.systems.Components;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BackpackEntity extends Entity implements Ownable, NamedScreenHandlerFactory {

    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(BackpackEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private UUID owner;

    public BackpackEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setYaw(this.random.nextFloat() * 360.0F);
    }

    public BackpackEntity(World world, double x, double y, double z, ItemStack stack) {
        this(MEntities.BACKPACK, world);
        this.setPosition(x, y, z);
        this.setStack(stack);
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public Text getName() {
        Text text = this.getCustomName();
        return text != null ? text : Text.translatable(this.getStack().getTranslationKey());
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
    }

    @Override
    public boolean collidesWith(Entity other) {
        return BoatEntity.canCollide(this, other);
    }

    @Override
    public boolean isCollidable() {
        return this.age > 20;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (entity instanceof BackpackEntity) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }
    }

    @Override
    public void tick() {
        if (this.getStack().isEmpty()) {
            this.discard();
        } else {
            super.tick();
            this.prevX = this.getX();
            this.prevY = this.getY();
            this.prevZ = this.getZ();
            Vec3d vec3d = this.getVelocity();
            float f = this.getStandingEyeHeight() - 0.11111111F;
            if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > (double) f) {
                this.applyWaterBuoyancy();
            } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double) f) {
                this.applyLavaBuoyancy();
            } else if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
            }

            if (this.getWorld().isClient) {
                this.noClip = false;
            } else {

                if (this.getStack().getItem() instanceof AbstractBackpackItem be) {
                    if (be.isInventoryEmpty(this.getStack())) {
                        this.discard();
                        this.dropStack(this.getStack());
                    }
                }
                this.noClip = !this.getWorld().isSpaceEmpty(this, this.getBoundingBox().contract(1.0E-7));
                if (this.noClip) {
                    this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
                }
            }

            if (!this.isOnGround() || this.getVelocity().horizontalLengthSquared() > 1.0E-5F || (this.age + this.getId()) % 4 == 0) {
                this.move(MovementType.SELF, this.getVelocity());
                float g = 0.98F;
                if (this.isOnGround()) {
                    g = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.98F;
                }

                this.setVelocity(this.getVelocity().multiply(g, 0.98, g));
                if (this.isOnGround()) {
                    Vec3d vec3d2 = this.getVelocity();
                    if (vec3d2.y < 0.0) {
                        this.setVelocity(vec3d2.multiply(1.0, -0.5, 1.0));
                    }
                }
            }
            this.velocityDirty = this.velocityDirty | this.updateWaterState();
            if (!this.getWorld().isClient) {
                double d = this.getVelocity().subtract(vec3d).lengthSquared();
                if (d > 0.01) {
                    this.velocityDirty = true;
                }
            }
            this.checkBlockCollision();
            List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(0.2F, -0.01F, 0.2F), EntityPredicates.canBePushedBy(this));
            if (!list.isEmpty()) {
                for (int j = 0; j < list.size(); j++) {
                    Entity entity = list.get(j);
                    if (!entity.hasPassenger(this)) {
                        this.pushAwayFrom(entity);
                    }
                }
            }
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!player.getWorld().isClient && this.age > 20 && !player.isSpectator()) {
            var ne = this.getStack();
            var inv = player.getComponent(Components.PLAYER_INVENTORY).getInventory();
            if (ne.getItem() instanceof AbstractBackpackItem bpn) {
                if (!player.isSneaking()) {
                    player.openHandledScreen(this);
                    return ActionResult.SUCCESS;
                }
                var st = ItemStack.EMPTY;
                int slot = 0;
                if (bpn.slot == AbstractBackpackItem.PackSlot.Backpack)
                    st = inv.getBags().getStack(0);
                else {
                    st = inv.getBags().getStack(1);
                    slot = 1;
                    if (!st.isEmpty()) {
                        slot = 2;
                        st = inv.getBags().getStack(2);
                    }
                }
                var old = st.copyAndEmpty();
                if (old.getItem() instanceof AbstractBackpackItem abp && abp.isInventoryEmpty(old)) {
                    player.dropItem(old, true);
                } else
                    dropItemBackpack(player, old);
                inv.getBags().setStack(slot, ne.copyAndEmpty());
                inv.clearCache();
                this.discard();
                if (!player.getWorld().isClient() && !player.isSilent()) {
                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, player.getSoundCategory(), 1.0F, 1.0F);
                }
                return ActionResult.CONSUME;
            } else {
                if (inv.insterStack(this.getStack().copy())) {
                    this.discard();
                    if (!player.getWorld().isClient() && !player.isSilent())
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, player.getSoundCategory(), 1.0F, 1.0F);
                    return ActionResult.SUCCESS;
                } else
                    return ActionResult.FAIL;
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    public static BackpackEntity dropItemBackpack(PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        } else {
            if (player.getWorld().isClient) {
                player.swingHand(Hand.MAIN_HAND);
                return null;
            } else {

                double d = player.getEyeY() - 0.3F;
                BackpackEntity itemEntity = new BackpackEntity(player.getWorld(), player.getX(), d, player.getZ(), stack);
                itemEntity.setOwner(player.getUuid());


                float f = 0.3F;
                float g = MathHelper.sin(player.getPitch() * (float) (Math.PI / 180.0));
                float h = MathHelper.cos(player.getPitch() * (float) (Math.PI / 180.0));
                float i = MathHelper.sin(player.getYaw() * (float) (Math.PI / 180.0));
                float j = MathHelper.cos(player.getYaw() * (float) (Math.PI / 180.0));
                float k = player.getRandom().nextFloat() * (float) (Math.PI * 2);
                float l = 0.02F * player.getRandom().nextFloat();
                itemEntity.setVelocity(
                        (double) (-i * h * 0.3F) + Math.cos(k) * (double) l,
                        -g * 0.3F + 0.1F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.1F,
                        (double) (j * h * 0.3F) + Math.sin(k) * (double) l
                );
                player.getWorld().spawnEntity(itemEntity);
                return itemEntity;
            }
        }
    }

    private void applyWaterBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * 0.99F, vec3d.y + (double) (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.99F);
    }

    private void applyLavaBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * 0.95F, vec3d.y + (double) (vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.95F);
    }

    /**
     * Returns the item stack contained in this item entity.
     */
    public ItemStack getStack() {
        return this.getDataTracker().get(STACK);
    }

    /**
     * Sets the item stack contained in this item entity to {@code stack}.
     */
    public void setStack(ItemStack stack) {
        this.getDataTracker().set(STACK, stack);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (STACK.equals(data)) {
            this.getStack().setHolder(this);
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.containsUuid("Owner")) {
            this.owner = nbt.getUuid("Owner");
        }
        NbtCompound nbtCompound = nbt.getCompound("Item");
        this.setStack(ItemStack.fromNbt(nbtCompound));
        if (this.getStack().isEmpty()) {
            this.discard();
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.owner != null) {
            nbt.putUuid("Owner", this.owner);
        }
        if (!this.getStack().isEmpty()) {
            nbt.put("Item", this.getStack().writeNbt(new NbtCompound()));
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        return this.owner != null && this.getWorld() instanceof ServerWorld serverWorld ? serverWorld.getEntity(this.owner) : null;
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        if (getStack().getItem() instanceof AbstractBackpackItem ab) {
            var inv = ab.getInventory(getStack());
            return new GenericContainer(syncId, player, new GridContainer("container", inv, ab.inventoryWidth(getStack()), ab.inventoryHeight(getStack()))) {
                @Override
                public void onClosed(PlayerEntity player) {
                    if (!BackpackEntity.this.isAlive()) {
                        if (!player.getWorld().isClient) {
                            player.dropItem(this.getCursorStack(), true);
                            for (var i = 0; i < inv.size(); i++)
                                player.dropItem(inv.getStack(i), false);
                        }
                    }
                }
            };
        }
        return null;

    }
}
