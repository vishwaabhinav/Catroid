/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.UtilToken;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.ActionBarSherlockCompat;
import com.actionbarsherlock.internal.view.menu.ActionMenuItem;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.jayway.android.robotium.solo.Solo;

public class UiTestUtils {
	private static ProjectManager projectManager = ProjectManager.getInstance();
	private static SparseIntArray brickCategoryMap;

	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";
	public static final String PROJECTNAME1 = "testingproject1";
	public static final String PROJECTNAME2 = "testingproject2";
	public static final String PROJECTNAME3 = "testingproject3";
	public static final String PROJECTDESCRIPTION1 = "testdescription1";
	public static final String PROJECTDESCRIPTION2 = "testdescription2";
	public static final String PROJECTDESCRIPTION3 = "testdescription3";
	public static final String DEFAULT_TEST_PROJECT_NAME_MIXED_CASE = "TeStPROjeCt";
	public static final String COPIED_PROJECT_NAME = "copiedProject";
	public static final String JAPANESE_PROJECT_NAME = "これは例の説明です。";

	private static final int ACTION_BAR_SPINNER_INDEX = 0;
	private static final int ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX = 0;

	public static enum FileTypes {
		IMAGE, SOUND, ROOT
	};

	private UiTestUtils() {
	};

	public static void enterText(Solo solo, int editTextIndex, String text) {
		solo.sleep(50);
		final EditText editText = solo.getEditText(editTextIndex);
		solo.getCurrentActivity().runOnUiThread(new Runnable() {
			public void run() {
				editText.setInputType(InputType.TYPE_NULL);
			}
		});
		solo.clearEditText(editTextIndex);
		solo.enterText(editTextIndex, text);
		solo.sleep(50);
	}

	/**
	 * Clicks on the EditText given by editTextId, inserts the integer value and closes the Dialog
	 * 
	 * @param editTextId
	 *            The ID of the EditText to click on
	 * @param value
	 *            The value you want to put into the EditText
	 */
	public static void insertIntegerIntoEditText(Solo solo, int editTextId, int value) {
		insertValue(solo, editTextId, value + "");
	}

	/**
	 * Clicks on the EditText given by editTextId, inserts the double value and closes the Dialog
	 * 
	 * @param editTextIndex
	 *            The ID of the EditText to click on
	 * @param value
	 *            The value you want to put into the EditText
	 */
	public static void insertDoubleIntoEditText(Solo solo, int editTextIndex, double value) {
		insertValue(solo, editTextIndex, value + "");
	}

	private static void insertValue(Solo solo, int editTextIndex, String value) {
		solo.clickOnEditText(editTextIndex);
		solo.sleep(50);
		solo.clearEditText(editTextIndex);
		solo.enterText(editTextIndex, value);
	}

	public static void clickEnterClose(Solo solo, int editTextIndex, String value) {
		solo.clickOnEditText(editTextIndex);
		solo.clearEditText(0);
		solo.enterText(0, value);
		String buttonPositiveText = solo.getString(R.string.ok);
		// if click is not successful, try workaround
		try {
			solo.clickOnText(buttonPositiveText);
		} catch (AssertionFailedError e) {
			solo.sendKey(Solo.ENTER);
		}
		solo.sleep(50);
	}

	private static void initBrickCategoryMap() {
		brickCategoryMap = new SparseIntArray();

		brickCategoryMap.put(R.string.brick_place_at_x, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_set_x, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_set_y, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_change_x_by, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_change_y_by, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_go_back, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_come_to_front, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_if_on_edge_bounce, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_move_n_steps, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_turn_left, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_turn_right, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_point_in_direction, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_point_to, R.string.category_motion);
		brickCategoryMap.put(R.string.brick_glide, R.string.category_motion);

		brickCategoryMap.put(R.string.brick_set_look, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_set_size_to, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_change_size_by, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_hide, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_show, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_set_ghost_effect, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_set_brightness, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_change_brightness, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_clear_graphic_effect, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_say, R.string.category_looks);
		brickCategoryMap.put(R.string.brick_think, R.string.category_looks);

		brickCategoryMap.put(R.string.brick_play_sound, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_stop_all_sounds, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_set_volume_to, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_change_volume_by, R.string.category_sound);
		brickCategoryMap.put(R.string.brick_speak, R.string.category_sound);

		brickCategoryMap.put(R.string.brick_when_started, R.string.category_control);
		brickCategoryMap.put(R.string.brick_when, R.string.category_control);
		brickCategoryMap.put(R.string.brick_wait, R.string.category_control);
		brickCategoryMap.put(R.string.brick_broadcast_receive, R.string.category_control);
		brickCategoryMap.put(R.string.brick_broadcast, R.string.category_control);
		brickCategoryMap.put(R.string.brick_broadcast_wait, R.string.category_control);
		brickCategoryMap.put(R.string.brick_note, R.string.category_control);
		brickCategoryMap.put(R.string.brick_forever, R.string.category_control);
		brickCategoryMap.put(R.string.brick_repeat, R.string.category_control);

		brickCategoryMap.put(R.string.brick_motor_action, R.string.category_lego_nxt);
	}

	public static int getBrickCategory(Solo solo, int brickStringId) {
		if (brickCategoryMap == null) {
			initBrickCategoryMap();
		}

		Integer brickCategoryid = brickCategoryMap.get(brickStringId);
		if (brickCategoryid == null) {
			String brickString = solo.getCurrentActivity().getString(brickStringId);
			throw new RuntimeException("No category was found for brick string \"" + brickString + "\".\n"
					+ "Please check brick string or add brick string to category map");
		}

		return brickCategoryMap.get(brickStringId);
	}

	public static void addNewBrick(Solo solo, int brickStringId) {
		int categoryStringId = getBrickCategory(solo, brickStringId);
		addNewBrick(solo, categoryStringId, brickStringId);
	}

	public static void addNewBrick(Solo solo, int categoryStringId, int brickStringId) {
		addNewBrick(solo, categoryStringId, brickStringId, 0);
	}

	public static void addNewBrick(Solo solo, int categoryStringId, int brickStringId, int nThElement) {
		clickOnBottomBar(solo, R.id.button_add);
		if (!solo.waitForText(solo.getCurrentActivity().getString(categoryStringId), 0, 5000)) {
			fail("Text not shown in 5 secs!");
		}
		solo.clickOnText(solo.getCurrentActivity().getString(categoryStringId));
		if (!solo.waitForText(solo.getCurrentActivity().getString(brickStringId), nThElement, 5000)) {
			fail("Text not shown in 5 secs!");
		}
		solo.clickOnText(solo.getCurrentActivity().getString(brickStringId), nThElement, true);
		solo.sleep(500);
	}

	public static List<Brick> createTestProject() {
		int xPosition = 457;
		int yPosition = 598;
		double size = 0.8;

		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");

		Script testScript = new StartScript(firstSprite);

		ArrayList<Brick> brickList = new ArrayList<Brick>();
		brickList.add(new HideBrick(firstSprite));
		brickList.add(new ShowBrick(firstSprite));
		brickList.add(new SetSizeToBrick(firstSprite, size));
		brickList.add(new GoNStepsBackBrick(firstSprite, 1));
		brickList.add(new ComeToFrontBrick(firstSprite));
		brickList.add(new PlaceAtBrick(firstSprite, xPosition, yPosition));

		for (Brick brick : brickList) {
			testScript.addBrick(brick);
		}

		firstSprite.addScript(testScript);

		project.addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);

		return brickList;
	}

	public static void createEmptyProject() {
		Project project = new Project(null, DEFAULT_TEST_PROJECT_NAME);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		projectManager.setProject(project);
		projectManager.setCurrentSprite(firstSprite);
		projectManager.setCurrentScript(testScript);
	}

	/**
	 * saves a file into the project folder
	 * if project == null or "" file will be saved into Catroid folder
	 * 
	 * @param project
	 *            Folder where the file will be saved, this folder should exist
	 * @param name
	 *            Name of the file
	 * @param fileID
	 *            the id of the file --> needs the right context
	 * @param context
	 * @param type
	 *            type of the file: 0 = imagefile, 1 = soundfile
	 * @return the file
	 * @throws IOException
	 */
	public static File saveFileToProject(String project, String name, int fileID, Context context, FileTypes type) {

		boolean withChecksum = true;
		String filePath;
		if (project == null || project.equalsIgnoreCase("")) {
			filePath = Constants.DEFAULT_ROOT + "/";
		} else {
			switch (type) {
				case IMAGE:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + Constants.IMAGE_DIRECTORY + "/";
					break;
				case SOUND:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/" + Constants.SOUND_DIRECTORY + "/";
					break;
				case ROOT:
					filePath = Constants.DEFAULT_ROOT + "/" + project + "/";
					withChecksum = false;
					break;
				default:
					filePath = Constants.DEFAULT_ROOT + "/";
					break;
			}
		}
		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID),
				Constants.BUFFER_8K);

		try {
			File file = new File(filePath + name);
			file.getParentFile().mkdirs();
			file.createNewFile();

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), Constants.BUFFER_8K);
			byte[] buffer = new byte[Constants.BUFFER_8K];
			int length = 0;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.flush();
			out.close();

			String checksum;
			if (withChecksum) {
				checksum = Utils.md5Checksum(file) + "_";
			} else {
				checksum = "";
			}

			File tempFile = new File(filePath + checksum + name);
			file.renameTo(tempFile);

			return tempFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean clearProject(String projectname) {
		File directory = new File(Constants.DEFAULT_ROOT + "/" + projectname);
		if (directory.exists()) {
			return UtilFile.deleteDirectory(directory);
		}
		return false;
	}

	public static Project createProject(String projectName, ArrayList<Sprite> spriteList, Context context) {
		Project project = new Project(context, projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		for (Sprite sprite : spriteList) {
			ProjectManager.getInstance().addSprite(sprite);
		}

		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	public static void clearAllUtilTestProjects() {
		projectManager.setFileChecksumContainer(new FileChecksumContainer());
		File directory = new File(Constants.DEFAULT_ROOT + "/" + PROJECTNAME1);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + PROJECTNAME2);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + PROJECTNAME3);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + DEFAULT_TEST_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "defaultProject");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "standardProjekt");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "My first project");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + "Mein erstes Projekt");
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + DEFAULT_TEST_PROJECT_NAME_MIXED_CASE);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + COPIED_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}

		directory = new File(Constants.DEFAULT_ROOT + "/" + JAPANESE_PROJECT_NAME);
		if (directory.exists()) {
			UtilFile.deleteDirectory(directory);
		}
	}

	public static void clickOnActionBar(Solo solo, int imageButtonId) {
		if (Build.VERSION.SDK_INT < 15) {
			solo.waitForView(LinearLayout.class);
			LinearLayout linearLayout = (LinearLayout) solo.getView(imageButtonId);
			solo.clickOnView(linearLayout);
		} else {
			solo.clickOnActionBarItem(imageButtonId);
		}
	}

	/**
	 * This method can be used in 2 ways. Either to click on an action item
	 * (icon), or to click on an item in the overflow menu. So either pass a
	 * String + ID --OR-- a String + 0.
	 * 
	 * @param solo
	 *            Use Robotium functionality
	 * @param overflowMenuItemName
	 *            Name of the overflow menu item
	 * @param overflowMenuItemId
	 *            ID of an action item (icon)
	 */
	public static void openActionMode(Solo solo, String overflowMenuItemName, int menuItemId) {
		if (overflowMenuItemName != null && menuItemId != 0) {

			if (solo.getView(menuItemId) == null) {
				solo.clickOnMenuItem(overflowMenuItemName, true);
			} else {
				UiTestUtils.clickOnActionBar(solo, menuItemId);
			}
		} else { // From overflow menu
			solo.clickOnMenuItem(overflowMenuItemName, true);
		}
		solo.sleep(400);
	}

	public static void acceptAndCloseActionMode(Solo solo) {
		solo.clickOnImage(ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX);
	}

	/**
	 * Due to maintainability reasons you should use this method to open an options menu.The way to open an options menu
	 * might differ in future.
	 */
	public static void openOptionsMenu(Solo solo) {
		solo.sendKey(Solo.MENU);
		solo.sleep(200);
	}

	public static void clickOnBottomBar(Solo solo, int buttonId) {
		solo.waitForView(LinearLayout.class);
		LinearLayout linearLayout = (LinearLayout) solo.getView(buttonId);
		solo.clickOnView(linearLayout);
	}

	public static File createTestMediaFile(String filePath, int fileID, Context context) throws IOException {

		File testImage = new File(filePath);

		if (!testImage.exists()) {
			testImage.createNewFile();
		}

		InputStream in = context.getResources().openRawResource(fileID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Constants.BUFFER_8K);

		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;

		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
	}

	public static void createValidUser(Context context) {
		try {
			String testUser = "testUser" + System.currentTimeMillis();
			String testPassword = "pwspws";
			String testEmail = testUser + "@gmail.com";

			String token = UtilToken.calculateToken(testUser, testPassword);
			boolean userRegistered = ServerCalls.getInstance().registerOrCheckToken(testUser, testPassword, testEmail,
					"de", "at", token);

			assert (userRegistered);

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			sharedPreferences.edit().putString(Constants.TOKEN, token).commit();

		} catch (WebconnectionException e) {
			e.printStackTrace();
			fail("Error creating test user.");
		}
	}

	// Stage methods
	public static void compareByteArrays(byte[] firstArray, byte[] secondArray) {
		assertEquals("Length of byte arrays not equal", firstArray.length, secondArray.length);
		assertEquals("Arrays don't have same content.", firstArray[0], secondArray[0], 10);
		assertEquals("Arrays don't have same content.", firstArray[1], secondArray[1], 10);
		assertEquals("Arrays don't have same content.", firstArray[2], secondArray[2], 10);
		assertEquals("Arrays don't have same content.", firstArray[3], secondArray[3], 10);
	}

	public static void comparePixelArrayWithPixelScreenArray(byte[] pixelArray, byte[] screenArray, int x, int y,
			int screenWidth, int screenHeight) {
		assertEquals("Length of pixel array not 4", 4, pixelArray.length);
		int convertedX = x + (screenWidth / 2);
		int convertedY = y + (screenHeight / 2);
		byte[] screenPixel = new byte[4];
		for (int i = 0; i < 4; i++) {
			screenPixel[i] = screenArray[(convertedX * 3 + convertedX + convertedY * screenWidth * 4) + i];
		}
		assertEquals("Pixels don't have same content.", pixelArray[0], screenPixel[0], 10);
		assertEquals("Pixels don't have same content.", pixelArray[1], screenPixel[1], 10);
		assertEquals("Pixels don't have same content.", pixelArray[2], screenPixel[2], 10);
		assertEquals("Pixels don't have same content.", pixelArray[3], screenPixel[3], 10);
	}

	/**
	 * Returns the absolute pixel y coordinates of elements from a listview
	 */
	public static ArrayList<Integer> getListItemYPositions(final Solo solo) {
		return getListItemYPositions(solo, 0);
	}

	/**
	 * Returns the absolute pixel y coordinates of elements from a listview
	 * with a given index
	 */
	public static ArrayList<Integer> getListItemYPositions(final Solo solo, int listViewIndex) {
		ArrayList<Integer> yPositionList = new ArrayList<Integer>();
		if (!solo.waitForView(ListView.class, 0, 10000, false)) {
			fail("ListView not shown in 10 secs!");
		}

		ArrayList<ListView> listViews = solo.getCurrentListViews();
		if (listViews.size() <= listViewIndex) {
			fail("Listview Index wrong");
		}
		ListView listView = listViews.get(listViewIndex);

		for (int i = 0; i < listView.getChildCount(); ++i) {
			View currentViewInList = listView.getChildAt(i);

			Rect globalVisibleRectangle = new Rect();
			currentViewInList.getGlobalVisibleRect(globalVisibleRectangle);
			int middleYPosition = globalVisibleRectangle.top + globalVisibleRectangle.height() / 2;
			yPositionList.add(middleYPosition);
		}

		return yPositionList;
	}

	public static int getAddedListItemYPosition(Solo solo) {
		ArrayList<Integer> yPositionsList = getListItemYPositions(solo, 1);
		int middleYPositionIndex = yPositionsList.size() / 2;

		return yPositionsList.get(middleYPositionIndex);
	}

	public static void longClickAndDrag(final Solo solo, final float xFrom, final float yFrom, final float xTo,
			final float yTo, final int steps) {
		final Activity activity = solo.getCurrentActivity();
		Handler handler = new Handler(activity.getMainLooper());

		handler.post(new Runnable() {

			public void run() {
				MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_DOWN, xFrom, yFrom, 0);
				activity.dispatchTouchEvent(downEvent);
			}
		});

		solo.sleep(ViewConfiguration.getLongPressTimeout() + 200);

		handler.post(new Runnable() {
			public void run() {
				double offsetX = xTo - xFrom;
				offsetX /= steps;
				double offsetY = yTo - yFrom;
				offsetY /= steps;
				for (int i = 0; i <= steps; i++) {
					float x = xFrom + (float) (offsetX * i);
					float y = yFrom + (float) (offsetY * i);
					MotionEvent moveEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
							MotionEvent.ACTION_MOVE, x, y, 0);
					activity.dispatchTouchEvent(moveEvent);

					solo.sleep(20);
				}
			}
		});

		solo.sleep(steps * 20 + 200);

		handler.post(new Runnable() {

			public void run() {
				MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_UP, xTo, yTo, 0);
				activity.dispatchTouchEvent(upEvent);
			}
		});

		solo.clickInList(0); // needed because of bug(?) in Nexus S 2.3.6
		solo.sleep(1000);
	}

	private static class ProjectWithCatrobatLanguageVersion extends Project {
		static final long serialVersionUID = 1L;
		private final float catrobatLanguageVersion;

		@SuppressWarnings("unused")
		public ProjectWithCatrobatLanguageVersion() {
			catrobatLanguageVersion = 0.3f;
		}

		public ProjectWithCatrobatLanguageVersion(String name, float catrobatLanguageVersion) {
			super(null, name);
			this.catrobatLanguageVersion = catrobatLanguageVersion;
		}

		@Override
		public float getCatrobatLanguageVersion() {
			return catrobatLanguageVersion;
		}
	}

	public static boolean createTestProjectOnLocalStorageWithCatrobatLanguageVersion(float catrobatLanguageVersion) {
		Project project = new ProjectWithCatrobatLanguageVersion(DEFAULT_TEST_PROJECT_NAME, catrobatLanguageVersion);
		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript(firstSprite);

		firstSprite.addScript(testScript);
		project.addSprite(firstSprite);

		ProjectManager.INSTANCE.setFileChecksumContainer(new FileChecksumContainer());
		ProjectManager.INSTANCE.setProject(project);
		ProjectManager.INSTANCE.setCurrentSprite(firstSprite);
		ProjectManager.INSTANCE.setCurrentScript(testScript);
		return ProjectManager.INSTANCE.saveProject();
	}

	public static void goToHomeActivity(Activity activity) {
		Intent intent = new Intent(activity, MainMenuActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}

	public static void clickOnHomeActionBarButton(Solo solo) {
		if (Build.VERSION.SDK_INT < 15) {
			Activity activity = solo.getCurrentActivity();

			ActionMenuItem logoNavItem = new ActionMenuItem(activity, 0, android.R.id.home, 0, 0, "");
			ActionBarSherlockCompat actionBarSherlockCompat = (ActionBarSherlockCompat) Reflection.invokeMethod(
					SherlockFragmentActivity.class, activity, "getSherlock", null, null);
			actionBarSherlockCompat.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, logoNavItem);
		} else {
			solo.clickOnActionBarHomeButton();
		}
	}

	public static void getIntoSpritesFromMainMenu(Solo solo) {
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.sleep(300);

		String continueString = solo.getString(R.string.main_menu_continue);
		solo.waitForText(continueString);

		solo.clickOnButton(continueString);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
	}

	public static void getIntoProgramMenuFromMainMenu(Solo solo, int spriteIndex) {
		getIntoSpritesFromMainMenu(solo);
		solo.sleep(200);

		solo.clickInList(spriteIndex);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
	}

	public static void getIntoSoundsFromMainMenu(Solo solo) {
		getIntoSoundsFromMainMenu(solo, 0);
	}

	public static void getIntoSoundsFromMainMenu(Solo solo, int spriteIndex) {
		getIntoProgramMenuFromMainMenu(solo, spriteIndex);

		solo.clickOnText(solo.getString(R.string.sounds));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		solo.sleep(200);
	}

	public static void getIntoLooksFromMainMenu(Solo solo) {
		getIntoLooksFromMainMenu(solo, 0, false);
	}

	public static void getIntoLooksFromMainMenu(Solo solo, boolean isBackground) {
		getIntoLooksFromMainMenu(solo, 0, isBackground);
	}

	public static void getIntoLooksFromMainMenu(Solo solo, int spriteIndex, boolean isBackground) {
		getIntoProgramMenuFromMainMenu(solo, spriteIndex);

		String textToClickOn = "";

		if (isBackground) {
			textToClickOn = solo.getString(R.string.backgrounds);
		} else {
			textToClickOn = solo.getString(R.string.looks);
		}
		solo.clickOnText(textToClickOn);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		solo.sleep(200);
	}

	public static void getIntoScriptActivityFromMainMenu(Solo solo) {
		getIntoScriptActivityFromMainMenu(solo, 0);
	}

	public static void getIntoScriptActivityFromMainMenu(Solo solo, int spriteIndex) {
		getIntoProgramMenuFromMainMenu(solo, spriteIndex);

		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.waitForView(ListView.class);
		solo.sleep(200);
	}

	public static boolean clickOnTextInList(Solo solo, String text) {
		solo.sleep(300);
		ArrayList<TextView> textViews = solo.getCurrentTextViews(solo.getView(android.R.id.list));
		for (int i = 0; i < textViews.size(); i++) {
			TextView view = textViews.get(i);
			if (view.getText().toString().equalsIgnoreCase(text)) {
				solo.clickOnView(view);
				return true;
			}
		}
		return false;
	}

	public static boolean longClickOnTextInList(Solo solo, String text) {
		solo.sleep(300);
		ArrayList<TextView> textViews = solo.getCurrentTextViews(solo.getView(android.R.id.list));
		for (int i = 0; i < textViews.size(); i++) {
			TextView view = textViews.get(i);
			if (view.getText().toString().equalsIgnoreCase(text)) {
				solo.clickLongOnView(view);
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns to the main screen.
	 * This method should be called in tearDown() in tests which use Robotium.
	 * See explanation here:
	 * http://stackoverflow.com/questions/7851351/robotium-in-the-suite-of-tests-each-next-test-is-
	 * affected-by-the-previous-test
	 */
	public static void goBackToHome(Instrumentation instrumentation) {
		boolean more = true;
		while (more) {
			try {
				instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
			} catch (SecurityException e) { // Done, at Home.
				more = false;
			}
		}
	}

	public static void cropImage(String pathToImageFile, int sampleSize) throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;
		Bitmap imageBitmap = BitmapFactory.decodeFile(pathToImageFile, options);
		File imageFile = new File(pathToImageFile);
		StorageHandler.saveBitmapToImageFile(imageFile, imageBitmap);
	}

	public static ListView getScriptListView(Solo solo) {
		return solo.getCurrentListViews().get(1);
	}

	public static void waitForFragment(Solo solo, int fragmentRootLayoutId) {
		waitForFragment(solo, fragmentRootLayoutId, 5000);
	}

	public static void waitForFragment(Solo solo, int fragmentRootLayoutId, int timeout) {
		boolean fragmentFoundInTime = solo.waitForView(solo.getView(fragmentRootLayoutId), timeout, true);
		if (!fragmentFoundInTime) {
			fail("Fragment was not loaded");
		}
	}

	public static void changeToFragmentViaActionbar(Solo solo, String currentSpinnerItem, String itemToSwitchTo) {
		solo.clickOnText(currentSpinnerItem);
		solo.sleep(50);
		solo.clickOnText(itemToSwitchTo);
		solo.sleep(50);
	}

	public static IcsSpinner getActionbarSpinnerOnPreHoneyComb(Solo solo) {
		ArrayList<View> activityViews = solo.getViews();
		IcsSpinner spinner = null;
		for (View viewToCheck : activityViews) {
			if (viewToCheck instanceof IcsSpinner) {
				spinner = (IcsSpinner) viewToCheck;
				break;
			}
		}
		if (spinner == null) {
			fail("no spinner found");
		}
		return spinner;
	}

	public static void clickOnActionBarSpinnerItem(Solo solo, int itemIndex) {
		if (Build.VERSION.SDK_INT < 15) {
			IcsSpinner spinner = UiTestUtils.getActionbarSpinnerOnPreHoneyComb(solo);
			int activeSpinnerItemIndex = spinner.getSelectedItemPosition();
			String itemToClickOnText = spinner.getAdapter().getItem(activeSpinnerItemIndex + itemIndex).toString();
			UiTestUtils.changeToFragmentViaActionbar(solo,
					spinner.getItemAtPosition(activeSpinnerItemIndex).toString(), itemToClickOnText);
		} else {
			solo.pressSpinnerItem(ACTION_BAR_SPINNER_INDEX, itemIndex);
		}
	}

	public static int getActionBarSpinnerItemCount(Solo solo) {
		if (Build.VERSION.SDK_INT < 15) {
			return UiTestUtils.getActionbarSpinnerOnPreHoneyComb(solo).getAdapter().getCount();
		} else {
			return solo.getCurrentSpinners().get(ACTION_BAR_SPINNER_INDEX).getAdapter().getCount();
		}
	}
}
