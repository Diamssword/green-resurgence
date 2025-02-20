package com.diamssword.greenresurgence.http;

import com.diamssword.greenresurgence.GreenResurgence;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.HttpAuthenticationService;
import net.minecraft.entity.player.PlayerEntity;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class APIService {
    public static final String url= GreenResurgence.CONFIG.SkinServerURL();
    private static String token;

    /**
     * Authentifie ou ré-autentifie le serveur à l'API
     * @return true si l'auth est un succés
     */
    public static CompletableFuture<Boolean> login()
    {
        var ob=new JsonObject();
        ob.addProperty("key",GreenResurgence.CONFIG.ServerSideApiKey());
       return postRequest(url+"/api/auth","",ob).thenApply(rep->{
            if(rep.statusCode()==200) {
                token = JsonParser.parseString(rep.body()).getAsJsonObject().get("token").getAsString();
                        return true;
            }
             return false;
        });
    }

    /**
     * Use a code to validate a skin to the api_server
     * @param player
     * @param code
     * @return true if the skin was processed
     */
    public static CompletableFuture<Boolean> validateSkin(PlayerEntity player,String code)
    {
        var ob=new JsonObject();
        ob.addProperty("code",code);
        ob.addProperty("uuid",player.getGameProfile().getId().toString());

        return postRequest(url+"/api/player/validate",token,ob).thenApply(v->{
            if(v.statusCode()==403) {
                try {
                return login().thenApply(c -> {
                    if (!c)
                        return false;
                    else {
                        try {
                            return validateSkin(player, code).get();
                        } catch (ExecutionException | InterruptedException e) {
                            return false;
                        }
                    }
                }).get();
                } catch (ExecutionException | InterruptedException e) {
                    return false;
                }
            }
            else return v.statusCode() == 200;
        });
    }
    /**
     * Use a code to link player account to the website
     * @param player
     * @param code
     * @return true if succesfully linked
     */
    public static CompletableFuture<Boolean> linkAccount(PlayerEntity player,String code)
    {
        var ob=new JsonObject();
        ob.addProperty("code",code);
        ob.addProperty("uuid",player.getGameProfile().getId().toString());

        return postRequest(url+"/api/player/link",token,ob).thenApply(v->{
            if(v.statusCode()==403) {
                try {
                    return login().thenApply(c -> {
                        if (!c)
                            return false;
                        else {
                            try {
                                return validateSkin(player, code).get();
                            } catch (ExecutionException | InterruptedException e) {
                                return false;
                            }
                        }
                    }).get();
                } catch (ExecutionException | InterruptedException e) {
                    return false;
                }
            }
            else return v.statusCode() == 200;
        });
    }
    public static CompletableFuture<HttpResponse<String>> getRequest(String url, String token) {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", token==null?"":token)
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

    }
    public static CompletableFuture<HttpResponse<String>> postRequest(String url, String token, JsonObject body) {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", token==null?"":token)
                    .build();
       return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

    }
}
