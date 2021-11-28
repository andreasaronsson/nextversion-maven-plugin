package nu.aron.next;

import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoteVersionTest implements RemoteVersion {

    @Test
    void validVersionInString() {
        assertEquals(Option.none(), versionFromString(""));
        assertEquals(Option.none(), versionFromString("some nonsense"));
        assertEquals("1", versionFromString(RELEASE_OPEN + "1" + RELEASE_CLOSE).get());
    }
}
