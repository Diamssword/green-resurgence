package com.diamssword.greenresurgence.systems.equipement;

public interface IEquipmentBlueprint {

	String getEquipmentType();

	String getEquipmentSubtype();

	default IEquipmentDef getEquipment() {
		return Equipments.getEquipment(getEquipmentType(), getEquipmentSubtype()).get();
	}
}
