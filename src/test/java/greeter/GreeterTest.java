package greeter;

import dador.HelloRequest;
import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.quicktheories.QuickTheory.qt;
import static org.quicktheories.generators.SourceDSL.strings;


public class GreeterTest {

    private Greeter greeter = Greeter.directGreeter();

    @Test
    public void checkStrings() {
        qt()
                .forAll(strings().allPossible().ofLengthBetween(0, 1000))
                .as(s -> greeter.greet(s))
                .check(Objects::nonNull);
    }

    @Test
    public void checkObjs() {
        qt()
                .forAll(strings().allPossible().ofLengthBetween(0, 100))
                .as(s -> HelloRequest.newBuilder().setName(s).build())
                .checkAssert(req -> assertThat(greeter.replyTo(req).getMessage(), not(isEmptyOrNullString())));
    }
}