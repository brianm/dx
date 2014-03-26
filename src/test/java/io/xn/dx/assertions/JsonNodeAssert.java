package io.xn.dx.assertions;

import com.fasterxml.jackson.databind.JsonNode;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class JsonNodeAssert extends AbstractAssert<JsonNodeAssert, JsonNode>
{
    protected JsonNodeAssert(final JsonNode actual)
    {
        super(actual, JsonNodeAssert.class);
    }

    public static JsonNodeAssert assertThat(JsonNode node) {
        return new JsonNodeAssert(node);
    }

    public JsonNodeAssert isArray() {
        if (!actual.isArray()) {
            failWithMessage("expected array, got %s", actual.getNodeType().toString());
        }
        return this;
    }

    public JsonNodeAssert isObject() {
        if (!actual.isObject()) {
            failWithMessage("expected object, got %s", actual.getNodeType().toString());
        }
        return this;
    }

    public JsonNodeAssert isTextual() {
        if (!actual.isTextual()) {
            failWithMessage("expected text, got %s", actual.getNodeType().toString());
        }
        return this;
    }

    public JsonNodeAssert textEquals(final String expected)
    {
        isTextual();
        Assertions.assertThat(actual.textValue()).isEqualTo(expected);
        return this;
    }

    public JsonNodeAssert hasField(final String key)
    {
        isObject();

        JsonNode node = actual.get(key);
        if (node.isNull()) {
            failWithMessage("expected field %s but it isn't there", key);
        }

        return this;
    }

    public JsonNodeAssert hasSize(final int size)
    {
        isArray();

        if (actual.size() != size) {
            failWithMessage("expected array of size %s but got size %s", size, actual.size());
        }

        return this;
    }
}
