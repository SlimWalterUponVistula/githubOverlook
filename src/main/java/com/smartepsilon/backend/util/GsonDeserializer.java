package com.smartepsilon.backend.util;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonDeserializer {

	public static final String ISO_REPO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	public static final Gson GSON_WITH_LOCAL_DT_DESERIALIZER_AND_ISO_DATE_TIME = new GsonBuilder()
			.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
				public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
						throws JsonParseException {
					return ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()).toLocalDateTime();
				}
			}).setDateFormat(ISO_REPO_DATE_TIME_FORMAT).create();

	public static <T> T extractEntityAndUnmarshal(Response response, Class<T> entityClass) {
		return make(response.readEntity(String.class), entityClass);
	}

	private static <T> T make(String jsonString, Class<T> resultClass) {
		return GSON_WITH_LOCAL_DT_DESERIALIZER_AND_ISO_DATE_TIME.fromJson(jsonString, resultClass);
	}
}
