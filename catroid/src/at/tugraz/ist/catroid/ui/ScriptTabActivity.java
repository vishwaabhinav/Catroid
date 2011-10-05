/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.dialogs.AddBrickDialog;
import at.tugraz.ist.catroid.ui.dialogs.BrickCategoryDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameCostumeDialog;
import at.tugraz.ist.catroid.ui.dialogs.RenameSoundDialog;
import at.tugraz.ist.catroid.utils.ActivityHelper;

public class ScriptTabActivity extends TabActivity implements OnDismissListener {
	protected ActivityHelper activityHelper;

	private TabHost tabHost;
	public SoundInfo selectedSoundInfo;
	private RenameSoundDialog renameSoundDialog;
	public CostumeData selectedCostumeData;
	private RenameCostumeDialog renameCostumeDialog;
	public String selectedCategory;
	public static final int DIALOG_RENAME_COSTUME = 0;
	public static final int DIALOG_RENAME_SOUND = 1;
	public static final int DIALOG_BRICK_CATEGORY = 2;
	public static final int DIALOG_ADD_BRICK = 3;

	private void setupTabHost() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scripttab);

		setupTabHost();
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		Intent intent; // Reusable Intent for each tab

		intent = new Intent().setClass(this, ScriptActivity.class);
		setupTab(R.drawable.ic_tab_scripts, this.getString(R.string.scripts), intent);
		intent = new Intent().setClass(this, CostumeActivity.class);
		int costumeIcon;
		if (ProjectManager.getInstance().getCurrentSprite().getName()
				.equalsIgnoreCase(this.getString(R.string.background))) {
			costumeIcon = R.drawable.ic_tab_background;
		} else {
			costumeIcon = R.drawable.ic_tab_costumes;
		}
		setupTab(costumeIcon, this.getString(R.string.costumes), intent);
		intent = new Intent().setClass(this, SoundActivity.class);
		setupTab(R.drawable.ic_tab_sounds, this.getString(R.string.sounds), intent);

		setUpActionBar();
		if (getLastNonConfigurationInstance() != null) {
			selectedCategory = (String) ((ArrayList<?>) getLastNonConfigurationInstance()).get(0);
			selectedCostumeData = (CostumeData) ((ArrayList<?>) getLastNonConfigurationInstance()).get(1);
			selectedSoundInfo = (SoundInfo) ((ArrayList<?>) getLastNonConfigurationInstance()).get(2);
		}
	}

	@Override
	public ArrayList<Object> onRetainNonConfigurationInstance() {
		ArrayList<Object> savedMember = new ArrayList<Object>();
		savedMember.add(selectedCategory);
		savedMember.add(selectedCostumeData);
		savedMember.add(selectedSoundInfo);
		return savedMember;
	}

	private void setUpActionBar() {
		activityHelper = new ActivityHelper(this);

		String title = this.getResources().getString(R.string.sprite_name) + " "
				+ ProjectManager.getInstance().getCurrentSprite().getName();
		activityHelper.setupActionBar(false, title);

		activityHelper.addActionButton(R.id.btn_action_add_sprite, R.drawable.ic_plus_black, null, false);

		activityHelper.addActionButton(R.id.btn_action_play, R.drawable.ic_play_black, new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ScriptTabActivity.this, StageActivity.class);
				startActivity(intent);
			}
		}, false);
	}

	private void setupTab(Integer drawableId, final String tag, Intent intent) {
		View tabview = createTabView(drawableId, tabHost.getContext(), tag);

		TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		tabHost.addTab(setContent);

	}

	private static View createTabView(Integer id, final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.activity_tabscriptactivity_tabs, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		if (id != null) {
			tv.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0);
		}
		return view;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
			case DIALOG_RENAME_SOUND:
				if (selectedSoundInfo != null) {
					renameSoundDialog = new RenameSoundDialog(this);
					dialog = renameSoundDialog.createDialog(selectedSoundInfo);
				}
				break;
			case DIALOG_RENAME_COSTUME:
				if (selectedCostumeData != null) {
					renameCostumeDialog = new RenameCostumeDialog(this);
					dialog = renameCostumeDialog.createDialog(selectedCostumeData);
				}
				break;
			case DIALOG_BRICK_CATEGORY:
				dialog = new BrickCategoryDialog(this);
				dialog.setOnDismissListener(this);
				break;
			case DIALOG_ADD_BRICK:
				if (selectedCategory != null) {
					dialog = new AddBrickDialog(this, selectedCategory);
				}
				break;
			default:
				dialog = null;
				break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
			case DIALOG_RENAME_SOUND:
				EditText soundTitleInput = (EditText) dialog.findViewById(R.id.dialog_rename_sound_editText);
				soundTitleInput.setText(selectedSoundInfo.getTitle());
				break;
			case DIALOG_RENAME_COSTUME:
				EditText costumeTitleInput = (EditText) dialog.findViewById(R.id.dialog_rename_costume_editText);
				costumeTitleInput.setText(selectedCostumeData.getCostumeName());
				break;
		}
	}

	public void handlePositiveButtonRenameSound(View v) {
		renameSoundDialog.handleOkButton();
	}

	public void handleNegativeButtonRenameSound(View v) {
		dismissDialog(DIALOG_RENAME_SOUND);
	}

	public void handlePositiveButtonRenameCostume(View v) {
		renameCostumeDialog.handleOkButton();
	}

	public void handleNegativeButtonRenameCostume(View v) {
		dismissDialog(DIALOG_RENAME_COSTUME);
	}

	public void onDismiss(DialogInterface dialogInterface) {
		((ScriptActivity) getCurrentActivity()).updateAdapterAfterAddNewBrick(dialogInterface);
	}

}