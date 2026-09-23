package cs2110;

import java.time.LocalDateTime;

import static cs2110.DataUtilities.*;
import static cs2110.DataUtilities.DedupPolicy.*;
import static cs2110.DataUtilities.SearchPolicy.*;

/**
 * Methods utilizing tools from `DataUtilities` to enable interesting queries on `View` array data.
 */
public class DataAnalysis {

    /**
     * Returns an array comprising the first view recorded for each video.
     */
    static View[] firstVideoViews(View[] views) {
        View[] byTime = deduplicatingSort(views, BY_TIMESTAMP, KEEP_ALL);
        return deduplicatingSort(byTime, BY_VIDEO_ID, KEEP_FIRST);
    }

    /**
     * Returns the total number of views that the video with the given `videoID` has had.
     */
    static int totalViews(View[] views, String videoID) {
        View[] byVidID = deduplicatingSort(views, BY_VIDEO_ID, KEEP_ALL);
        View key = new View(null, videoID, null); // can't search for videoID directly; stick in "dummy" View object
        int first = binarySearch(byVidID, key, BY_VIDEO_ID, LEFT);
        int last = binarySearch(byVidID, key, BY_VIDEO_ID, RIGHT);
        return last - first;
    }

    /**
     * Returns the number of distinct users who viewed at least one video at a timestamp `t` with
     * `start <= t <= end`.
     */
    @SuppressWarnings("SameParameterValue")
    static int countDistinctUsersInTimeInterval(View[] views, LocalDateTime start, LocalDateTime end) {
        // TODO 5: Implement this method according to its specifications. Your definition must use
        //  the `binarySearch()`, `copyOfRange()`, and/or `deduplicatingSort()` methods of the
        //  `DataUtilities` class to manipulate the array data. You may not directly access the
        //  array contents. Label each line of with its worst-case runtime complexity.
        View[] byTimestamp = deduplicatingSort(views, BY_TIMESTAMP, KEEP_ALL); // O(N log N)
        View startKey = new View(null, null, start); // O(1)
        int first = binarySearch(byTimestamp, startKey, BY_TIMESTAMP, LEFT); // O(log N)
        View endKey = new View(null, null, end); // O(1)
        int last = binarySearch(byTimestamp, endKey, BY_TIMESTAMP, RIGHT); // O(log N)
        View[] viewsInRange = copyOfRange(byTimestamp, first, last); // O(N) worst case
        View[] eachUser = deduplicatingSort(viewsInRange, BY_USER_ID, KEEP_FIRST); // O(N log N) worst case
        return eachUser.length; // O(1)


    }

    /**
     * Returns an array of length `k` containing the Views of the last `k` distinct videos that the
     * given `userID` has watched (in any order). If that video has been watched more than once by
     * the user, then the View corresponding to the latest watch is included. More formally (to
     * account for possible ties), this method returns an array of `k` Views such that (1) the user
     * of each View has the given `userID`, (2) the `videoID`s of these Views are distinct, and (3)
     * for each videoID `v1` in this array, if this user viewed `v2` strictly after `v1`, then a
     * view of `v2` will also be present in the array. If `userID` has viewed fewer than `k`
     * distinct videos, then a shorter array containing their latest View of each video is returned.
     */
    @SuppressWarnings("SameParameterValue")
    static View[] lastKViewedByUser(View[] views, String userID, int k) {
        // TODO 6: Implement this method according to its specifications. Your definition must use
        //  the `binarySearch()`, `copyOfRange()`, and/or `deduplicatingSort()` methods of the
        //  `DataUtilities` class to manipulate the array data. You may not directly access the array
        //  contents. Label each line of your definition with its worst-case runtime complexity.
        View[] byUserID = deduplicatingSort(views, BY_USER_ID, KEEP_ALL); // O(N log N)
        View userKey = new View(userID, null, null); // O(1)
        int first = binarySearch(byUserID, userKey, BY_USER_ID, LEFT); // O(log N)
        int last = binarySearch(byUserID, userKey, BY_USER_ID, RIGHT); // O(log N)
        View[] userViews = copyOfRange(byUserID, first, last);  // O(N) worst case
        View[] timeUserViews = deduplicatingSort(userViews, BY_TIMESTAMP, KEEP_ALL); // O(N log N) worst case
        View[] lastViews = deduplicatingSort(timeUserViews, BY_VIDEO_ID, KEEP_LAST); // O(N log N) worst case
        View[] lastViewsTime = deduplicatingSort(lastViews, BY_TIMESTAMP, KEEP_ALL); // O(N log N) worst case
        int startIndex = lastViewsTime.length - k; // O(1)
        if (startIndex < 0) { // O(1)
            startIndex = 0; // O(1)
        }
        int endIndex = lastViewsTime.length; // O(1)
        View[] result = copyOfRange(lastViewsTime, startIndex, endIndex); // O(k) worst case
        return result; // O(1)


    }

    /**
     * Returns the `userID` of an individual who has the most recorded views of the video with
     * the given `videoID` in the `views` array. Returns `null` if there are no recorded views for
     * that video. The contents of `views` are not modified by this method.
     */
    @SuppressWarnings("SameParameterValue")
    static String mostObsessedViewer(View[] views, String videoID) {
        // TODO 7: Implement this method according to its specifications. Make sure to add a comment
        //  documenting the invariant of each loop that you write. Your definition must have a
        //  worst-case runtime complexity of `O(N + M log M)`, where `N = views.length` and `M` is
        //  the number of entries of `views` with the given `videoID`.
        View[] posMatches = new View[views.length];
        int numViews = 0;
        for (int i = 0; i < views.length; i++) {
            String thisVideoID = views[i].videoID();
            if (thisVideoID.equals(videoID)) {
                posMatches[numViews] = views[i];
                numViews++;
            }
        }
        if (numViews == 0) {
            return null;
        }
        View[] matches = copyOfRange(posMatches, 0, numViews);
        View[] matchByUser = deduplicatingSort(matches, BY_USER_ID, KEEP_ALL);
        int currentRun = 1;
        String currentUser = matchByUser[0].userID();
        int bestRun = currentRun;
        String bestUser = currentUser;
        for (int j = 1; j < matchByUser.length; j++) {
            if (matchByUser[j].userID().equals(currentUser)) {
                currentRun++;
            } else {
                if (currentRun > bestRun) {
                    bestRun = currentRun;
                    bestUser = currentUser;
                }
                currentUser = matchByUser[j].userID();
                currentRun = 1;
            }
        }
        if (currentRun > bestRun) {
            bestRun = currentRun;
            bestUser = currentUser;
        }
        return bestUser;
    }
}