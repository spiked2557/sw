/*
 * This is an example test project created in Eclipse to test NotePad which is a sample 
 * project located in AndroidSDK/samples/android-11/NotePad
 * 
 * 
 * You can run these test cases either on the emulator or on device. Right click
 * the test project and select Run As --> Run As Android JUnit Test
 * 
 * @author Renas Reda, renas.reda@gmail.com
 * 
 */

package com.tugraz.test;

import java.util.Random;

import com.tugraz.parkreminder.*;
import com.jayway.android.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;

public class ParkingReminderTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;

	public ParkingReminderTest() {
		super(MainActivity.class);

	}

	@Override
	public void setUp() throws Exception {
		// setUp() is run before a test case is started.
		// This is where the solo object is created.
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		// tearDown() is run after a test case has finished.
		// finishOpenedActivities() will finish all the activities that have been opened during the test execution.
		solo.finishOpenedActivities();
	}

	public void testApp1_checkMapAndMemoryTest() throws Exception {
		solo.assertCurrentActivity("Expected Activity", "MainActivity");
		solo.drag(0, 100, 50, 50, 200);
		solo.drag(500, 300, 800, 800, 50);
		solo.drag(300, 300, 500, 800, 30);
		solo.drag(300, 300, 800, 500, 10);
		solo.clickOnScreen(700, 200);
		solo.drag(0, 100, 50, 50, 200);
		solo.assertMemoryNotLow();
	}

	public void testApp2_checkTimer() throws Exception {
		solo.assertCurrentActivity("Expected Activity", "MainActivity");
		solo.drag(0, 100, 50, 50, 100);
		solo.clickOnScreen(400, 1150);
		solo.drag(0, 100, 50, 50, 100);
		solo.drag(300, 300, 650, 400, 20);
		solo.drag(300, 300, 650, 850, 20);
		solo.drag(460, 460, 650, 400, 50);
		solo.drag(460, 460, 650, 400, 50);
		solo.drag(460, 460, 650, 850, 50);
		solo.clickOnScreen(380, 900);
		solo.drag(0, 100, 50, 50, 200);
		boolean actual = solo.searchToggleButton("+ Alarm hinzufügen");
		boolean expected = false;
		assertEquals("There should be an active Alarm.", expected, actual);
	}

	public void testApp3_mapInteractionAndCancelTimer() throws Exception {
		solo.assertCurrentActivity("Expected Activity", "MainActivity");
		solo.drag(0, 100, 50, 50, 200);
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			solo.drag(random.nextInt(600) + 100, random.nextInt(600) + 100, random.nextInt(700) + 250, random.nextInt(700) + 250, random.nextInt(20) + 10);
		}
		solo.clickOnScreen(700, 200);
		solo.drag(0, 100, 50, 50, 100);
		solo.clickOnScreen(600, 100);
		solo.drag(0, 100, 50, 50, 200);
		boolean actual = solo.searchText("+ Alarm hinzufügen");
		boolean expected = true;
		assertEquals("There should be no active Alarm.", expected, actual);

	}
}
