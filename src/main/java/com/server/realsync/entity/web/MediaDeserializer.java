/**
 * 
 */
package com.server.realsync.entity.web;

/**
 * 
 */
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class MediaDeserializer extends JsonDeserializer<Media> {

    @Override
    public Media deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonToken token = p.currentToken();

        if (token == JsonToken.START_OBJECT) {
            // Normal case: media is an object
            return mapper.readValue(p, Media.class);
        } else if (token == JsonToken.START_ARRAY) {
            // Empty array case: skip array and return null
            if (p.nextToken() == JsonToken.END_ARRAY) {
                return null;
            } else {
                // Handle non-empty array if needed
                return mapper.readValue(p, Media.class); // Or throw exception if not supported
            }
        }

        return null; // fallback
    }
}
