package se.citerus.dddsample.tracking.core.domain.model.cargo;

import junit.framework.TestCase;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.*;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ItineraryTest extends TestCase {

  Voyage voyage, wrongVoyage, pacific, transcontinental, atlantic;

  protected void setUp() throws Exception {

    pacific = new Voyage.Builder(new VoyageNumber("4567"), SHANGHAI).
      addMovement(LONGBEACH, new Date(1), new Date(2)).
      build();

    transcontinental = new Voyage.Builder(new VoyageNumber("4567"), LONGBEACH).
      addMovement(CHICAGO, new Date(1), new Date(2)).
      addMovement(NEWYORK, new Date(3), new Date(4)).
      build();

    atlantic = new Voyage.Builder(new VoyageNumber("4556"), NEWYORK).
      addMovement(ROTTERDAM, new Date(1), new Date(2)).
      addMovement(GOTHENBURG, new Date(3), new Date(4)).
      build();

    voyage = new Voyage.Builder(new VoyageNumber("0123"), SHANGHAI).
      addMovement(ROTTERDAM, new Date(1), new Date(2)).
      addMovement(GOTHENBURG, new Date(3), new Date(4)).
      build();

    wrongVoyage = new Voyage.Builder(new VoyageNumber("666"), NEWYORK).
      addMovement(STOCKHOLM, new Date(1), new Date(2)).
      addMovement(HELSINKI, new Date(3), new Date(4)).
      build();
  }

  public void testIfCargoIsOnTrack() {

    Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(voyage, SHANGHAI, ROTTERDAM, new Date(1), new Date(2)),
        new Leg(voyage, ROTTERDAM, GOTHENBURG, new Date(3), new Date(4))
      )
    );

    // HandlingActivity.Load(cargo, RECEIVE, SHANGHAI, toDate("2009-05-03"))
    //Happy path
    HandlingActivity receiveShanghai = new HandlingActivity(RECEIVE, SHANGHAI);
    assertTrue(itinerary.isExpectedActivity(receiveShanghai));

    HandlingActivity loadShanghai = new HandlingActivity(LOAD, SHANGHAI, voyage);
    assertTrue(itinerary.isExpectedActivity(loadShanghai));

    HandlingActivity unloadRotterdam = new HandlingActivity(UNLOAD, ROTTERDAM, voyage);
    assertTrue(itinerary.isExpectedActivity(unloadRotterdam));

    HandlingActivity loadRotterdam = new HandlingActivity(LOAD, ROTTERDAM, voyage);
    assertTrue(itinerary.isExpectedActivity(loadRotterdam));

    HandlingActivity unloadGothenburg = new HandlingActivity(UNLOAD, GOTHENBURG, voyage);
    assertTrue(itinerary.isExpectedActivity(unloadGothenburg));

    HandlingActivity claimGothenburg = new HandlingActivity(CLAIM, GOTHENBURG);
    assertTrue(itinerary.isExpectedActivity(claimGothenburg));

    //TODO Customs event can only be interpreted properly by knowing the destination of the cargo.
    // This can be inferred from the Itinerary, but it isn't definitive. So, do we answer based on
    // the end of the itinerary (even though this would probably not be used in the app) or do we
    // ignore this at itinerary level somehow and leave it purely as a cargo responsibility.
    // (See customsClearancePoint tests in CargoTest)
//    HandlingActivity customsGothenburg = new HandlingActivity(CUSTOMS, GOTHENBURG);
//    assertTrue(itinerary.isExpectedActivity(customsGothenburg));

    //Received at the wrong location
    HandlingActivity receiveHangzou = new HandlingActivity(RECEIVE, HANGZOU);
    assertFalse(itinerary.isExpectedActivity(receiveHangzou));

    //Loaded to onto the wrong ship, correct location
    HandlingActivity loadRotterdam666 = new HandlingActivity(LOAD, ROTTERDAM, wrongVoyage);
    assertFalse(itinerary.isExpectedActivity(loadRotterdam666));

    //Unloaded from the wrong ship in the wrong location
    HandlingActivity unloadHelsinki = new HandlingActivity(UNLOAD, HELSINKI, wrongVoyage);
    assertFalse(itinerary.isExpectedActivity(unloadHelsinki));

    HandlingActivity claimRotterdam = new HandlingActivity(CLAIM, ROTTERDAM);
    assertFalse(itinerary.isExpectedActivity(claimRotterdam));
  }

  public void testMatchingLeg() {
    Leg shanghaiToRotterdam = Leg.deriveLeg(voyage, SHANGHAI, ROTTERDAM);
    Leg rotterdamToGothenburg = Leg.deriveLeg(voyage, ROTTERDAM, GOTHENBURG);
    Itinerary itinerary = new Itinerary(shanghaiToRotterdam, rotterdamToGothenburg);

    assertThat(itinerary.legMatchOf(receiveIn(SHANGHAI)).leg(), is(shanghaiToRotterdam));
    assertThat(itinerary.legMatchOf(loadOnto(voyage).in(SHANGHAI)).leg(), is(shanghaiToRotterdam));
    assertThat(itinerary.legMatchOf(unloadOff(voyage).in(ROTTERDAM)).leg(), is(shanghaiToRotterdam));
    assertThat(itinerary.legMatchOf(claimIn(GOTHENBURG)).leg(), is(rotterdamToGothenburg));

    assertNull(itinerary.legMatchOf(loadOnto(wrongVoyage).in(SHANGHAI)).leg());
    assertNull(itinerary.legMatchOf(loadOnto(wrongVoyage).in(NEWYORK)).leg());

    assertNull(itinerary.legMatchOf(unloadOff(wrongVoyage).in(ROTTERDAM)).leg());
    assertNull(itinerary.legMatchOf(unloadOff(wrongVoyage).in(NEWYORK)).leg());

    assertNull(itinerary.legMatchOf(receiveIn(NEWYORK)).leg());
    assertNull(itinerary.legMatchOf(claimIn(NEWYORK)).leg());
  }

  public void testNextLeg() {
    Leg shanghaiToLongBeach = Leg.deriveLeg(pacific, SHANGHAI, LONGBEACH);
    Leg longBeachToNewYork = Leg.deriveLeg(transcontinental, LONGBEACH, NEWYORK);
    Leg newYorkToRotterdam = Leg.deriveLeg(atlantic, NEWYORK, ROTTERDAM);

    Itinerary itinerary = new Itinerary(shanghaiToLongBeach, longBeachToNewYork, newYorkToRotterdam);

    assertThat(itinerary.nextLeg(shanghaiToLongBeach), equalTo(longBeachToNewYork));
    assertThat(itinerary.nextLeg(longBeachToNewYork), equalTo(newYorkToRotterdam));
    assertNull(itinerary.nextLeg(newYorkToRotterdam));
  }

  public void testLatestLeg() {
    Leg shanghaiToLongBeach = Leg.deriveLeg(pacific, SHANGHAI, LONGBEACH);
    Leg longBeachToNewYork = Leg.deriveLeg(transcontinental, LONGBEACH, NEWYORK);
    Leg newYorkToRotterdam = Leg.deriveLeg(atlantic, NEWYORK, ROTTERDAM);

    Itinerary itinerary = new Itinerary(shanghaiToLongBeach, longBeachToNewYork, newYorkToRotterdam);

    HandlingActivity notOnRoute = HandlingActivity.loadOnto(pacific).in(STOCKHOLM);
    HandlingActivity loadInShanghai = HandlingActivity.loadOnto(pacific).in(SHANGHAI);
    HandlingActivity unloadInLongbeach = HandlingActivity.unloadOff(pacific).in(LONGBEACH);

    assertThat(itinerary.strictlyPriorOf(loadInShanghai, unloadInLongbeach), is(loadInShanghai));
    assertThat(itinerary.strictlyPriorOf(unloadInLongbeach, loadInShanghai), is(loadInShanghai));
    
    assertThat(itinerary.strictlyPriorOf(unloadInLongbeach, notOnRoute), is(unloadInLongbeach));
    assertThat(itinerary.strictlyPriorOf(notOnRoute, loadInShanghai), is(loadInShanghai));
    
    assertNull(itinerary.strictlyPriorOf(unloadInLongbeach, unloadInLongbeach));
  }

  public void testCreateItinerary() throws Exception {
    try {
      new Itinerary(new ArrayList<Leg>());
      fail("An empty itinerary is not OK");
    } catch (IllegalArgumentException iae) {
      //Expected
    }

    try {
      new Itinerary((List<Leg>) null);
      fail("Null itinerary is not OK");
    } catch (IllegalArgumentException iae) {
      //Expected
    }
  }

}