package io.xn.dx.assertions;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import org.assertj.core.api.Assertions;
import org.assertj.guava.api.OptionalAssert;

public class GdxAssertions extends Assertions
{
    public static JsonNodeAssert assertThat(JsonNode node) {
        return JsonNodeAssert.assertThat(node);
    }
    public static <T> OptionalAssert<T> assertThat(Optional<T> optional) { return new MyOptionalAssert(optional);}


    private static class MyOptionalAssert<T> extends OptionalAssert<T> {

        MyOptionalAssert(final Optional<T> actual)
        {
            super(actual);
        }
    }
}


