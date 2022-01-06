package com.example.vocabularyapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class VariableActivity extends AppCompatActivity {

    //binding nie dzialalo
    private VariableViewModel variableViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variable);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final VariableAdapter adapter = new VariableAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        variableViewModel = ViewModelProviders.of(this).get(VariableViewModel.class);
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

            final EditText dialogCreateEng = (EditText) createView.findViewById(R.id.variable_eng);
            final EditText dialogCreatePl = (EditText) createView.findViewById(R.id.variable_pl);
            final EditText dialogCreateCategory = (EditText) createView.findViewById(R.id.variable_category);


            alertDialogVariableCreate.setPositiveButton("Save", (dialogBox, id) -> {
                Variable variable = new Variable();
                variable.setWord_eng(dialogCreateEng.getText().toString());
                variable.setWord_pl(dialogCreatePl.getText().toString());
                variable.setCategory(dialogCreateCategory.getText().toString());
                variableViewModel.insert(variable);
            });

            alertDialogVariableCreate.setNegativeButton("Cancel", (dialogBox, id) -> dialogBox.cancel());


            AlertDialog alertDialog = alertDialogVariableCreate.create();
            alertDialog.show();
        });


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
        private Variable variable;

        public VariableHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.variable_list_item, parent, false));

            variableEngTextView = itemView.findViewById(R.id.vriable_eng);
            variablePlTextView = itemView.findViewById(R.id.variable_pl);
            variableCategoryTextView = itemView.findViewById(R.id.category);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void bind(Variable variable){
            this.variable = variable;
            variableEngTextView.setText(variable.getWord_eng());
            variablePlTextView.setText(variable.getWord_pl());
            variableCategoryTextView.setText(variable.getCategory());
        }


        @Override
        public void onClick(View view) {

            LayoutInflater layoutInflater = LayoutInflater.from(VariableActivity.this);
            View editView = layoutInflater.inflate(R.layout.variable_create_edit_item, null);
            AlertDialog.Builder alertDialogVariableEdit = new AlertDialog.Builder(VariableActivity.this);
            alertDialogVariableEdit.setTitle("Edit variable");
            alertDialogVariableEdit.setView(editView);

            final EditText dialogEditEng = (EditText) editView.findViewById(R.id.variable_eng);
            final EditText dialogEditPl = (EditText) editView.findViewById(R.id.variable_pl);
            final EditText dialogEditCategory = (EditText) editView.findViewById(R.id.variable_category);

            dialogEditEng.setText(variableEngTextView.getText());
            dialogEditPl.setText(variablePlTextView.getText());
            dialogEditCategory.setText(variableCategoryTextView.getText());

            alertDialogVariableEdit.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogBox, int id) {
                    //variableEngTextView.setText(dialogEditEng.getText().toString());
                    //variablePlTextView.setText(dialogEditPl.getText().toString());
                    //variableCategoryTextView.setText(dialogEditCategory.getText().toString());
                    //zapis w bazie
                    variable.setWord_eng(dialogEditEng.getText().toString());
                    variable.setWord_pl(dialogEditPl.getText().toString());
                    variable.setCategory(dialogEditCategory.getText().toString());
                    variableViewModel.update(variable);
                }
            });

            alertDialogVariableEdit.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogBox, int id) {
                    dialogBox.cancel();
                }
            });


            AlertDialog alertDialog = alertDialogVariableEdit.create();
            alertDialog.show();

        }

        @Override
        public boolean onLongClick(View view) {

            AlertDialog.Builder alertDialogVariableDelete = new AlertDialog.Builder(VariableActivity.this);

            alertDialogVariableDelete.setCancelable(false);
            alertDialogVariableDelete.setTitle("Delete variable");
            alertDialogVariableDelete.setMessage("Are you sure to delete?");

            alertDialogVariableDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogBox, int id) {
                    variableViewModel.delete(variable);
                }
            });

            alertDialogVariableDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogBox, int id) {
                    dialogBox.cancel();
                }
            });

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
            if (variables != null){
                return variables.size();
            }
            else {
                return 0;
            }
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