// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public final class FindMeetingQuery {

  /* query: returns a collection of TimeRanges that allow all meeting attendees to attend
  * the meeting.
  * requires: none
  * ensures: meeting times do not conflict with any non-optional meeting attendees
  */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    Collection<String> mandatoryAttendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();

    ArrayList<TimeRange> mandatoryBusyTimes = getBusyTimes(events, mandatoryAttendees);
    ArrayList<TimeRange> optionalBusyTimes = getBusyTimes(events, optionalAttendees);

    mergeBusyNestedIntervals(mandatoryBusyTimes);
    mergeBusyNestedIntervals(optionalBusyTimes);

    ArrayList<TimeRange> combinedBusyTimes = combine(mandatoryBusyTimes, optionalBusyTimes);
    ArrayList<TimeRange> freeTimes = invertTimes(combinedBusyTimes);
    ArrayList<TimeRange> meetingTimes = adjustForMeeting(freeTimes, request.getDuration());

    // if optional attendees cause no times to be available, default to mandatory attendees' schedule
    if(meetingTimes.isEmpty()) {
      return adjustForMeeting(invertTimes(mandatoryBusyTimes), request.getDuration());
    }

    return adjustForMeeting(freeTimes, request.getDuration());
  }

  /* combine: combine two lists while maintaining sort 
  * requires: L1 and L2 are sorted by start time
  * ensures: result merges L1 and L2 into one sorted list
  */
  private ArrayList<TimeRange> combine(ArrayList<TimeRange> L1, ArrayList<TimeRange> L2) {

    int prevSize = L1.size();

    for (TimeRange timeL1 : L1) {
      addToSorted(L2, timeL1);
    }

    return L2;
  }

  /* getBusyTimes: gets all times when specified attendees are busy
  * requires: none
  * ensures: result contains all times when relevant attendees are busy
  */
  private ArrayList<TimeRange> getBusyTimes(Collection<Event> events, Collection<String> attendees) {
    
    ArrayList<TimeRange> busyTimes = new ArrayList<TimeRange>();

    for (Event currentEvent : events) {

      // check that event involves at least one meeting attendee
      if(containsOne(currentEvent.getAttendees(), attendees)) {

        TimeRange eventRange = currentEvent.getWhen();
        busyTimes = addToSorted(busyTimes, eventRange);
      }
    }

    return busyTimes;
  }

  /* mergeBusyNestedIntervals: merges any nested/overlapping intervals into one TimeRange
  * using the max approach (so intervals from 8-9AM and 8:30-9:30 would be merged into one
  * interval from 8-9:30AM).
  * requires: times is sorted by start time
  * ensures: result is sorted and merges any overlapping/nested intervals
  */
  private ArrayList<TimeRange> mergeBusyNestedIntervals(ArrayList<TimeRange> times) {

    int length = times.size() - 1;

    for (int index = 0; index < length; index++) {
      
      TimeRange currentRange = times.get(index);
      TimeRange nextRange = times.get(index + 1);

      if (currentRange.overlaps(nextRange)) {

        int end = Math.max(currentRange.end(), nextRange.end());

        TimeRange combinedRange = TimeRange.fromStartEnd(currentRange.start(), end, false);
        times.remove(index);
        times.remove(index); // indices will shift down
        times.add(index, combinedRange);
      }

      length = times.size() - 1; // recalculate since size has changed
    }

    return times;
  }
  
  /* confirms that at least one meeting attendee is involved in the event */
  private boolean containsOne(Set<String> eventAttendees, Collection<String> meetingAttendees) {
    
    for (String attendee : meetingAttendees) {
      if(eventAttendees.contains(attendee)) {
        return true;
      }
    }

    return false;
  }

  /* adjustForMeeting: removes any free times that do not allow for entire meeting duration to finish
  * requires: none
  * ensures: all remaining TimeRanges can accomadate the duration of the meeting
  */
  private ArrayList<TimeRange> adjustForMeeting(ArrayList<TimeRange> allTimes, long meetingDuration) {

    Iterator<TimeRange> iter = allTimes.iterator();

    while (iter.hasNext()) {

      TimeRange currentRange = iter.next();
      long rangeDuration = currentRange.duration();
      
      if(meetingDuration > rangeDuration) {
        iter.remove();
      }
    }

    return allTimes;
  }

  /* addToSorted: adds the TimeRange to the list while maintaing times' order invariant
  * requires: times is sorted by start time
  * ensures: times has range inserted and is still sorted by start time
  */
  private ArrayList<TimeRange> addToSorted(ArrayList<TimeRange> times, TimeRange range) {
    
    int start = range.start();

    for(int index = 0; index < times.size(); index++) {
      
      if(start <= times.get(index).start()) {
        times.add(index, range);
        return times;
      }
    }

    times.add(range);
    return times;
  }

  /* invertTimes: constructs a list with TimeRanges complementary to those of parameter
  * requires: times is sorted by start time
  * ensures: result is sorted by start time and inverts the parameter
  */
  private ArrayList<TimeRange> invertTimes(ArrayList<TimeRange> times) {
    
    int start = TimeRange.START_OF_DAY;
    ArrayList<TimeRange> inverted = new ArrayList<TimeRange>();

    for(TimeRange range : times) {

      if (start <= range.start()) {
        TimeRange x = TimeRange.fromStartEnd(start, range.start(), false);
        start = range.end();

        inverted.add(x);
      }
    }

    inverted.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
    return inverted;
  }
}
