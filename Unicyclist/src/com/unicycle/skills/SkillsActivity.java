package com.unicycle.skills;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.unicycle.GalleryMenuAdapter;
import com.unicycle.R;
import com.unicycle.UnicyclistActivity;
import com.unicycle.locations.LocationsActivity;
import com.unicycle.locations.NewLocationActivity;

public class SkillsActivity extends Activity {
	
	private final String titleColor = "#ffbb33";
	private ViewFlipper page;
	private Animation fadeIn;
	private Animation fadeOut;
	private List<Skill> skillList;
	private ProgressDialog pd;
	private SkillsListAdapter skillsListAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skills);
        
        //Set up page containers
        page = (ViewFlipper) findViewById(R.id.flipper);
        fadeIn = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        
        //Set up main menu
        Gallery menu = (Gallery) findViewById(R.id.menu);
        menu.setAdapter(new GalleryMenuAdapter(this, new String[] {getString(R.string.skills),getString(R.string.goals),getString(R.string.accomplishments)},titleColor));
        menu.setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {	        	
	        	page.setInAnimation(fadeIn);
	        	page.setOutAnimation(fadeOut);
	        	page.setDisplayedChild(position);
	        }
	        @Override
	        public void onNothingSelected(AdapterView<?> arg0) {
	            // Do nothing
	        }
        });

        //Set up Skills list
        ListView skillsListView = (ListView) findViewById(R.id.fullList);
        Skills db = new Skills(this);
        skillList = db.getAllSkills();
        db.close();
        View footerView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.skill_list_add, null, false);
        skillsListView.addFooterView(footerView);
        skillsListAdapter = new SkillsListAdapter(SkillsActivity.this,R.layout.skill_list_item,skillList);
        skillsListView.setAdapter(skillsListAdapter);
    }

    public void onClick(View footerView) {
    	pd = ProgressDialog.show(SkillsActivity.this, "Opening..", "Please wait...", true, false);
    	startActivityForResult(new Intent(SkillsActivity.this, NewSkillActivity.class), UnicyclistActivity.CREATE_SKILL);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.skills, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newSkill:	
            	pd = ProgressDialog.show(SkillsActivity.this, "Opening...", "Please wait...", true, false);
            	startActivityForResult(new Intent(SkillsActivity.this, NewSkillActivity.class), UnicyclistActivity.CREATE_SKILL);
            	break;   
        }
        return true;
    }

    
    @Override
    protected void onResume() {
    	super.onResume();
		if (pd!=null) {
			pd.dismiss();
		}
    }
    
    @Override
    protected void onActivityResult(
        int aRequestCode, int aResultCode, Intent aData) {
        switch (aRequestCode) {
            case UnicyclistActivity.CREATE_SKILL:
            	if ((aData != null) && (aResultCode == Activity.RESULT_OK)) {
            		//Retrieve Data
            		String name = aData.getStringExtra("name");
            		String description = aData.getStringExtra("description");
            		int difficulty = aData.getIntExtra("difficulty", 0);
            		Skill skill = new Skill(name,description,difficulty);
            		//add to database
            		Skills db = new Skills(SkillsActivity.this);
            		skill.setId(db.addSkill(skill));
            		db.close();
            		//add to list in memory
            		skillList.add(skill);
            		skillsListAdapter.notifyDataSetChanged();
            	}
            	break;
        }
    }
    
	private class SkillsListAdapter extends ArrayAdapter<Skill> {
		
		private List<Skill> _skills;

		public SkillsListAdapter(Context context, int textViewResourceId,	List<Skill> skills) {
			super(context, textViewResourceId, skills);
			_skills = skills;
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.skill_list_item, null);
	        }
	      
	        Skill skill = _skills.get(position);
	        if (skill != null) {
	        	TextView skillName = (TextView) v.findViewById(R.id.skillName);
	        	TextView description = (TextView) v.findViewById(R.id.description);
	        	
	        	if (skillName != null) {
	        		skillName.setText(skill.getName());
	        	}
	        	if (description != null) {
	        		description.setText(skill.getDescription());
	        	}
	        	
	        }
	        return v;
	    }
    }
}
