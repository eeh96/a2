package cs2110;

import static cs2110.DataUtilities.*;
import static cs2110.DataUtilities.SearchPolicy.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class BinarySearchTest {

    private static final LocalDateTime TIME =
            LocalDateTime.of(2026, 1, 1, 12, 0);

    @DisplayName("Empty Array Returns Zero")
    @Test
    public void emptyArrayReturnsZero() {
        View[] views = {};
        View key = new View("b", null, null);

        assertEquals(0, binarySearch(views, key, BY_USER_ID, LEFT));
        assertEquals(0, binarySearch(views, key, BY_USER_ID, RIGHT));
    }

    @DisplayName("Responds Correctly If Key is Not Found")
    @Test
    public void missingKeys() {
        View[] views = {
                new View("b", "video1", TIME)
        };

        View before = new View("a", null, null);
        View matching = new View("b", null, null);
        View after = new View("c", null, null);

        assertEquals(0, binarySearch(views, before, BY_USER_ID, LEFT));
        assertEquals(0, binarySearch(views, before, BY_USER_ID, RIGHT));

        assertEquals(0, binarySearch(views, matching, BY_USER_ID, LEFT));
        assertEquals(1, binarySearch(views, matching, BY_USER_ID, RIGHT));

        assertEquals(1, binarySearch(views, after, BY_USER_ID, LEFT));
        assertEquals(1, binarySearch(views, after, BY_USER_ID, RIGHT));
    }

    @DisplayName("Right Search and Left Search Work Correctly with Duplicates")
    @Test
    public void duplicateVideoIDs() {
        // Sorted by videoID. Equivalent videoIDs have different users.
        View[] views = {
                new View("zoe", "a", TIME),
                new View("max", "b", TIME.plusHours(1)),
                new View("amy", "b", TIME.plusHours(2)),
                new View("leo", "b", TIME.plusHours(3)),
                new View("ben", "d", TIME.plusHours(4))
        };

        View[] original = copyOfRange(views, 0, views.length);
        View key = new View(null, "b", null);

        // The "b" entries occupy indices 1, 2, and 3.
        assertEquals(1, binarySearch(views, key, BY_VIDEO_ID, LEFT));
        assertArrayEquals(original, views);

        assertEquals(4, binarySearch(views, key, BY_VIDEO_ID, RIGHT));
        assertArrayEquals(original, views);
    }

    @DisplayName("Missing Timestamp Returns Correct Value")
    @Test
    public void missingTimestampReturnsInsertionIndex() {
        View[] views = {
                new View("d", "video4", TIME),
                new View("c", "video3", TIME.plusHours(2)),
                new View("b", "video2", TIME.plusHours(4)),
                new View("a", "video1", TIME.plusHours(6))
        };

        View key = new View(null, null, TIME.plusHours(3));

        // Three hours falls between indices 1 and 2.
        assertEquals(2, binarySearch(views, key, BY_TIMESTAMP, LEFT));
        assertEquals(2, binarySearch(views, key, BY_TIMESTAMP, RIGHT));
    }

    @DisplayName("Correctly Handles Boundary Keys")
    @Test
    public void handlesBoundaryKeys() {
        View[] views = {
                new View("b", "video1", TIME),
                new View("d", "video2", TIME),
                new View("f", "video3", TIME)
        };

        View before = new View("a", null, null);
        View first = new View("b", null, null);
        View last = new View("f", null, null);
        View after = new View("z", null, null);

        assertEquals(0, binarySearch(views, before, BY_USER_ID, LEFT));
        assertEquals(0, binarySearch(views, before, BY_USER_ID, RIGHT));

        assertEquals(0, binarySearch(views, first, BY_USER_ID, LEFT));
        assertEquals(1, binarySearch(views, first, BY_USER_ID, RIGHT));

        assertEquals(2, binarySearch(views, last, BY_USER_ID, LEFT));
        assertEquals(3, binarySearch(views, last, BY_USER_ID, RIGHT));

        assertEquals(3, binarySearch(views, after, BY_USER_ID, LEFT));
        assertEquals(3, binarySearch(views, after, BY_USER_ID, RIGHT));
    }
}