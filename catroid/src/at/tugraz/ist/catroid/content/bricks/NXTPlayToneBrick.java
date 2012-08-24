/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.fragment.FormulaEditorFragment;

public class NXTPlayToneBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private static final int MIN_FREQ_IN_HERTZ = 200;
	private static final int MAX_FREQ_IN_HERTZ = 14000;
	private static final int MIN_DURATION = 0;
	private static final int MAX_DURATION = Integer.MAX_VALUE;

	private Sprite sprite;

	private transient EditText editFreq;

	private Formula frequency;
	private Formula durationInSeconds;

	public NXTPlayToneBrick(Sprite sprite, int frequencyValue, int durationVaue) {
		this.sprite = sprite;

		this.frequency = new Formula(Integer.toString(frequencyValue));
		this.durationInSeconds = new Formula(Integer.toString(durationVaue));
	}

	public NXTPlayToneBrick(Sprite sprite, Formula frequencyFormula, Formula durationFormula) {
		this.sprite = sprite;

		this.frequency = frequencyFormula;
		this.durationInSeconds = durationFormula;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	@Override
	public void execute() {
		int frequencyValue = frequency.interpretInteger(MIN_FREQ_IN_HERTZ, MAX_FREQ_IN_HERTZ);
		int durationInMillisecondsValue = (int) durationInSeconds.interpretFloat(MIN_DURATION, MAX_DURATION) * 1000;

		LegoNXT.sendBTCPlayToneMessage(frequencyValue, durationInMillisecondsValue);

	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new NXTPlayToneBrick(getSprite(), frequency, durationInSeconds);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View brickView = View.inflate(context, R.layout.brick_nxt_play_tone, null);

		TextView textDuration = (TextView) brickView.findViewById(R.id.nxt_tone_duration_text_view);
		//		editDuration.setText(String.valueOf(durationInMs / 1000.0));
		durationInSeconds.setTextFieldId(R.id.nxt_tone_duration_edit_text);
		durationInSeconds.refreshTextField(brickView);
		//		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
		//				MAX_DURATION);
		//		dialogDuration.setOnDismissListener(this);
		//		dialogDuration.setOnCancelListener((OnCancelListener) context);
		//		editDuration.setOnClickListener(dialogDuration);

		textDuration.setOnClickListener(this);

		TextView textFreq = (TextView) brickView.findViewById(R.id.nxt_tone_freq_text_view);
		//		editFreq.setText(String.valueOf(hertz / 100));
		frequency.setTextFieldId(R.id.nxt_tone_freq_text_view);
		frequency.refreshTextField(brickView);

		editFreq.setOnClickListener(this);

		return brickView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.nxt_tone_freq_text_view:
				FormulaEditorFragment.showFragment(view, this, frequency);
				break;
			case R.id.nxt_tone_duration_text_view:
				FormulaEditorFragment.showFragment(view, this, durationInSeconds);
				break;
		}

	}

}
