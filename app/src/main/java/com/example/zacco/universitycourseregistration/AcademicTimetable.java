package com.example.zacco.universitycourseregistration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AcademicTimetable extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private EditText addTaskBox;
    private DatabaseReference databaseReference;
    private List<Course> courses;
    private Button filterButton;
    private Spinner departmentSpinner, termSpin;
    static String chosenSpecialty, chosenSemester;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_timetable);

        departmentSpinner = findViewById(R.id.spec);
        termSpin = findViewById(R.id.term);
        filterButton = findViewById(R.id.filterButton);
        courses = new ArrayList<Course>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Course");//.child("Course");
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Query queryAll = FirebaseDatabase.getInstance().getReference().child("Course");
        displayQuery(queryAll);
        filterButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenSpecialty = String.valueOf(departmentSpinner.getSelectedItem());
                chosenSemester =  String.valueOf(termSpin.getSelectedItem());
                if ((chosenSpecialty.equals("All") || chosenSpecialty.equals("-Select a Department-")) && (chosenSemester.equals("All") || chosenSemester.equals("-Select a Term-"))) {
                    Query queryAll = FirebaseDatabase.getInstance().getReference().child("Course");
                    displayQuery(queryAll);
                }
                else if((chosenSemester.equals("All") ||chosenSemester.equals("-Select a Term-")) && (!(chosenSpecialty.equals("All") ||chosenSpecialty.equals("-Select a Department-")))){
                    Query querySpec = FirebaseDatabase.getInstance().getReference().child("Course").orderByChild("Department").equalTo(chosenSpecialty);
                    displayQuery(querySpec);
                }
                else if((!(chosenSemester.equals("All") ||chosenSemester.equals("-Select a Term-"))) && (chosenSpecialty.equals("All") || chosenSpecialty.equals("-Select a Department-"))){
                    Query querySpec = FirebaseDatabase.getInstance().getReference().child("Course").orderByChild("Semester").equalTo(chosenSemester);
                    displayQuery(querySpec);
                }
                else{

                    final Query querySpec = FirebaseDatabase.getInstance().getReference().child("Course").orderByChild("dept_time").equalTo(chosenSpecialty + "_"+chosenSemester);


                    displayQuery(querySpec);
                }
            }
        });

    }
    public void displayQuery(Query query){
        final FirebaseRecyclerOptions<Course> options =
                new FirebaseRecyclerOptions.Builder<Course>()
                        .setQuery(query, new SnapshotParser<Course>() {
                            @NonNull
                            @Override
                            public Course parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                                String name = (String) map.get("Course Name");
                                String description = (String) map.get("Description");

                                //TODO what's wrong with capacity? When I try to set it, it errors.
                                //Is it because it is set to 0 and so it assumes it is set to a
                                // null or something like that?
                                //int capacity = (int) map.get("Capacity");

                                String location = (String) map.get("Location");
                                String prerequisites = (String) map.get("Prerequisites");
                                String prof = (String) map.get("Prof");
                                String profEmail = (String) map.get("Prof Email");
                                String semester = (String) map.get("Semester");
                                String time = (String) map.get("Time");
                                Course c = new Course(name, description, 0, location,
                                        prerequisites, prof, profEmail, semester, time);
                                System.out.println(c.toString());
                                return c;
                            }
                        })
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Course, CourseHolder>(options) {
            @Override
            public CourseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.course_list, parent, false);

                return new CourseHolder(view);
            }

            @Override
            protected void onBindViewHolder(CourseHolder holder, int position, Course model) {
                holder.bindCourse(model);
            }


        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

//    public void filterCourses(View v){
////        chosenSpecialty = String.valueOf(departmentSpinner.getSelectedItem());
////        chosenSemester =  String.valueOf(termSpin.getSelectedItem());
////        if((!chosenSpecialty.equals("All") || !chosenSpecialty.equals("-Select a Department-")) && ((!chosenSemester.equals("-Select a Term-") || !chosenSemester.equals("-Select a Term-")))){
////            Query queryTermSpec = FirebaseDatabase.getInstance().getReference().child("Course").orderByChild("Department").equalTo(chosenSpecialty)
////                    .orderByChild("Semester").equalTo(chosenSemester);
////            displayQuery(queryTermSpec);
////        }
////        else if((chosenSpecialty.equals("All") || chosenSpecialty.equals("-Select a Department-")) && (!chosenSemester.equals("-Select a Term-") || !chosenSemester.equals("-Select a Term-"))){
////            Query queryTermSpec = FirebaseDatabase.getInstance().getReference().child("Course").orderByChild("Department").equalTo(chosenSpecialty);
////            displayQuery(queryTermSpec);
////        }
////        else if((!chosenSpecialty.equals("All") || !chosenSpecialty.equals("-Select a Department-")) && (chosenSemester.equals("-Select a Term-") || chosenSemester.equals("-Select a Term-"))){
////            Query queryTermSpec = FirebaseDatabase.getInstance().getReference().child("Course").orderByChild("Semester").equalTo(chosenSemester);
////            displayQuery(queryTermSpec);
////        }
////        else{
////            Query queryTerm = FirebaseDatabase.getInstance().getReference().child("Course");
////            displayQuery(queryTerm);
////        }
////
////    }
}
