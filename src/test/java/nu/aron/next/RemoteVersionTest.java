package nu.aron.next;

import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoteVersionTest implements RemoteVersion {

    @Test
    void validVersionInString() {
        assertEquals(Option.none(), versionFromString(""));
        assertEquals(Option.none(), versionFromString("some nonsense"));
        assertEquals("1", versionFromString(releaseOpen + "1" + releaseClose).get());
    }
}
