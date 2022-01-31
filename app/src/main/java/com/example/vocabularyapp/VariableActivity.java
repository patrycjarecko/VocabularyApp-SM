package com.example.vocabularyapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import lombok.SneakyThrows;

public class VariableActivity extends AppCompatActivity {

    private VariableViewModel variableViewModel;
    private List<String> categories = new ArrayList<>();
    private List<String> statuses = HomeActivity.statuses;
    private String chosenCategory;
    private String chosenStatus;
    Spinner categorySpinner;
    Spinner statusSpinner;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variable);
        variableViewModel = ViewModelProviders.of(this).get(VariableViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final VariableAdapter adapter = new VariableAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        prepareCategories();

        //uzaleznic od wyboru jezyka
        variableViewModel.findAllEngVariables().observe(this, adapter::setVariables);

        SearchView searchView = findViewById(R.id.search_field);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.setVariables(findWorlds(query, Objects.requireNonNull(variableViewModel.findAllEngVariables().getValue())));
                return true;
            }


            @Override
            public boolean onQueryTextChange(String query) {
                adapter.setVariables(findWorlds(query, Objects.requireNonNull(variableViewModel.findAllEngVariables().getValue())));
                return true;
            }
        });

        FloatingActionButton addBookButton = findViewById(R.id.add_button);
        addBookButton.setOnClickListener(view -> {

            LayoutInflater layoutInflater = LayoutInflater.from(VariableActivity.this);
            View createView = layoutInflater.inflate(R.layout.variable_create_edit_item, null);
            AlertDialog.Builder alertDialogVariableCreate = new AlertDialog.Builder(VariableActivity.this);
            alertDialogVariableCreate.setTitle("Create variable");
            alertDialogVariableCreate.setView(createView);

            final EditText dialogCreateEng = createView.findViewById(R.id.variable_eng);
            final EditText dialogCreatePl = createView.findViewById(R.id.variable_pl);

            categorySpinner = createView.findViewById(R.id.spinner_category);
            statusSpinner = createView.findViewById(R.id.spinner_status);

            ArrayAdapter<String> categorySpinnerAdapter= new ArrayAdapter<>(VariableActivity.this, android.R.layout.simple_list_item_1, this.categories);
            categorySpinner.setAdapter(categorySpinnerAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    chosenCategory = (String) adapterView.getItemAtPosition(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Snackbar.make(createView, getString(R.string.nothing_selected), Snackbar.LENGTH_LONG).show();
                }
            });

            ArrayAdapter<String> statusSpinnerAdapter= new ArrayAdapter<>(VariableActivity.this, android.R.layout.simple_list_item_1, this.statuses);
            statusSpinner.setAdapter(statusSpinnerAdapter);
            statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    chosenStatus = (String) adapterView.getItemAtPosition(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Snackbar.make(createView, getString(R.string.nothing_selected), Snackbar.LENGTH_LONG).show();
                }
            });


            alertDialogVariableCreate.setPositiveButton("Save", (dialogBox, id) -> {
                if (dialogCreatePl.getText().toString().equals("") || dialogCreateEng.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Input is empty. Try again", Toast.LENGTH_SHORT).show();
                }
                else{
                    Variable variable = new Variable();
                    variable.setWord_eng(dialogCreateEng.getText().toString());
                    variable.setWord_pl(dialogCreatePl.getText().toString());
                    variable.setCategory(chosenCategory);
                    variable.setStatus(chosenStatus);
                    variableViewModel.insert(variable);
                }
            });

            alertDialogVariableCreate.setNegativeButton("Cancel", (dialogBox, id) -> dialogBox.cancel());
            AlertDialog alertDialog = alertDialogVariableCreate.create();
            alertDialog.show();
        });


    }

    private void prepareCategories() throws IOException {
        BufferedReader bufer = new BufferedReader(new InputStreamReader(getAssets().open("categories.txt")));
        String word;

        while ((word = bufer.readLine()) != null){
            this.categories.add(word);
        }
    }

    private List<Variable> findWorlds(String query, List<Variable> variables) {
        List<Variable> temp = new ArrayList<>();

        for (Variable variable : variables) {

            if (variable.getWord_eng().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                temp.add(variable);
            }

        }
        return temp;
    }

    private class VariableHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView variableEngTextView;
        private TextView variablePlTextView;
        private TextView variableCategoryTextView;
        private TextView variableStatusTextView;
        private Variable variable;

        public VariableHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.variable_list_item, parent, false));

            variableEngTextView = itemView.findViewById(R.id.variable_eng);
            variablePlTextView = itemView.findViewById(R.id.variable_pl);
            variableCategoryTextView = itemView.findViewById(R.id.category);
            variableStatusTextView = itemView.findViewById(R.id.status);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void bind(Variable variable){
            this.variable = variable;
            variableEngTextView.setText(variable.getWord_eng());
            variablePlTextView.setText(variable.getWord_pl());
            variableCategoryTextView.setText(variable.getCategory());
            variableStatusTextView.setText(variable.getStatus());
        }


        private void setArrayCategories(List<String> strings) {
            categories = strings;
        }
        private void setArrayStatuses(List<String> strings) {
            statuses = strings;
        }

        @Override
        public void onClick(View view) {

            LayoutInflater layoutInflater = LayoutInflater.from(VariableActivity.this);
            View editView = layoutInflater.inflate(R.layout.variable_create_edit_item, null);
            AlertDialog.Builder alertDialogVariableEdit = new AlertDialog.Builder(VariableActivity.this);
            alertDialogVariableEdit.setTitle("Edit variable");
            alertDialogVariableEdit.setView(editView);

            categorySpinner = editView.findViewById(R.id.spinner_category);
            statusSpinner = editView.findViewById(R.id.spinner_status);

            ArrayAdapter<String> categorySpinnerAdapter= new ArrayAdapter<>(VariableActivity.this, android.R.layout.simple_list_item_1, categories);
            categorySpinner.setAdapter(categorySpinnerAdapter);
            categorySpinner.setSelection(categorySpinnerAdapter.getPosition(variableCategoryTextView.getText().toString()));

            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    chosenCategory = (String) adapterView.getItemAtPosition(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Snackbar.make(findViewById(R.id.variable_create_edit_view), getString(R.string.nothing_selected), Snackbar.LENGTH_LONG).show();
                }
            });

            ArrayAdapter<String> statusSpinnerAdapter= new ArrayAdapter<>(VariableActivity.this, android.R.layout.simple_list_item_1, statuses);
            statusSpinner.setAdapter(statusSpinnerAdapter);
            statusSpinner.setSelection(statusSpinnerAdapter.getPosition(variableStatusTextView.getText().toString()));

            statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    chosenStatus = (String) adapterView.getItemAtPosition(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Snackbar.make(findViewById(R.id.variable_create_edit_view), getString(R.string.nothing_selected), Snackbar.LENGTH_LONG).show();
                }
            });


            final EditText dialogEditEng = editView.findViewById(R.id.variable_eng);
            final EditText dialogEditPl = editView.findViewById(R.id.variable_pl);

            dialogEditEng.setText(variableEngTextView.getText());
            dialogEditPl.setText(variablePlTextView.getText());

            alertDialogVariableEdit.setPositiveButton("Save", (dialogBox, id) -> {
                if (dialogEditPl.getText().toString().equals("") || dialogEditEng.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Input is empty. Try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    variable.setWord_eng(dialogEditEng.getText().toString());
                    variable.setWord_pl(dialogEditPl.getText().toString());
                    variable.setCategory(chosenCategory);
                    variable.setStatus(chosenStatus);
                    variableViewModel.update(variable);
                }
            });

            alertDialogVariableEdit.setNegativeButton("Cancel", (dialogBox, id) -> dialogBox.cancel());
            AlertDialog alertDialog = alertDialogVariableEdit.create();
            alertDialog.show();

        }

        @Override
        public boolean onLongClick(View view) {

            AlertDialog.Builder alertDialogVariableDelete = new AlertDialog.Builder(VariableActivity.this);

            alertDialogVariableDelete.setCancelable(false);
            alertDialogVariableDelete.setTitle("Delete variable");
            alertDialogVariableDelete.setMessage("Are you sure to delete?");

            alertDialogVariableDelete.setPositiveButton("Delete", (dialogBox, id) -> variableViewModel.delete(variable));
            alertDialogVariableDelete.setNegativeButton("Cancel", (dialogBox, id) -> dialogBox.cancel());

            AlertDialog alertDialog = alertDialogVariableDelete.create();
            alertDialog.show();

            return true;
        }

    }


    private class VariableAdapter extends RecyclerView.Adapter<VariableHolder>{
        private List<Variable> variables;


        @NonNull
        @Override
        public VariableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VariableHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull VariableHolder holder, int position) {
            if (variables != null){
                Variable variable = variables.get(position);
                holder.bind(variable);
            }
            else {
                Log.d("VariableActivity", "No variables");
            }
        }

        @Override
        public int getItemCount() {
            return variables != null ? variables.size() : 0;
        }

        void setVariables(List<Variable> variables){
            this.variables = variables;
            notifyDataSetChanged();
        }

        List<Variable> getVariables(){
            return variables;
        }

    }
}