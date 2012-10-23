package com.unicycle.skills;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.unicycle.R;
import com.unicycle.tags.Tag;
import com.unicycle.tags.Tags;

public class NewSkillActivity extends Activity {
	
	private EditText name;
	private EditText description;
	private TextView difficulty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_skill);
        
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        SeekBar setDifficulty = (SeekBar) findViewById(R.id.difficultySlider);
        difficulty = (TextView) findViewById(R.id.difficulty);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Button addButton = (Button) findViewById(R.id.saveButton);
        
        setDifficulty.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
				difficulty.setText(Integer.toString(progress));
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}
        });
        
        cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
        });
        
        addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
        		if (name.getText().toString().trim().length() == 0) {
        			new AlertDialog.Builder(NewSkillActivity.this)
        			.setTitle("Oops!")
        			.setMessage("You must provide a name for this skill.")
        			.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog,	int which) {
        				}
        			}).show();
        		} else {
	        		Intent _result = new Intent();
	        		_result.putExtra("name",name.getText().toString());
	        		_result.putExtra("description",description.getText().toString());
	        		_result.putExtra("difficulty",Integer.parseInt(difficulty.getText().toString()));
	        		setResult(Activity.RESULT_OK,_result);
	        		NewSkillActivity.this.finish();
        		}
			}
        });
    }
}
