package com.example.mateusz.lamimozgi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mateusz.lamimozgi.adapters.SudokuGridAdapter;
import com.example.mateusz.lamimozgi.items.SudokuCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SudokuActivity extends AppCompatActivity implements GameActivity {
    private GameApplication app;
    private SudokuCell[] content;
    private GridView gameBoard;
    private int BOARD_SIZE;
    private int positionInFocus = -1;
    private int lastPositionInFocus;
    private ArrayList<ArrayList<Integer>> group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (GameApplication) getApplication();
        String board = app.selectedStage.getStage();
        String save = app.selectedStage.getSave();
        String mark = app.selectedStage.getExtra();
        String type = app.selectedStage.getType();
        BOARD_SIZE = app.selectedStage.getWidth();
        groupMaker();
        fromPuzzleString(board, save, mark, type);
        setUpViews();
    }

    private void groupMaker() {
        ArrayList<ArrayList<Integer>> groups = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++){
            groups.add(new ArrayList<Integer>());
        }
        group = groups;
    }

    @Override
    public void fromPuzzleString(String string, String save, String mark, String type) {
        SudokuCell[] puz = new SudokuCell[string.length()];
        for (int i = 0; i < puz.length; i++) {
            int value = string.charAt(i) - '0';
            int saveValue = save.charAt(i) - '0';
            int markValue = mark.charAt(i) - '0';
            int typeValue = type.charAt(i) - '0';
            boolean isHighlighted = false;
            boolean isInitial = true;
            boolean isEven = false;

            if (value == 0) {
                isInitial = false;
            }
            if (!(saveValue == 0)){
                value = saveValue;
            }
            if (!(markValue == 0)){
                value = markValue;
                isHighlighted = true;
            }
            if (BOARD_SIZE == 6){
                isEven = typeValue==0||typeValue==3||typeValue==4;
            }else if (BOARD_SIZE == 9){
                isEven = typeValue==0||typeValue==2||typeValue==4||typeValue==6||typeValue==8;
            }else if (BOARD_SIZE == 12){
                isEven = typeValue==0||typeValue==2||typeValue==4||typeValue==6||typeValue==8||typeValue==10;
            }
            add(typeValue, i);
            SudokuCell sc = new SudokuCell(value, isInitial, isEven);
            sc.setHighlighted(isHighlighted);
            puz[i] = sc;
        }
        content = puz;
    }

    private void add(int typeValue, int e) {
        group.get(typeValue).add(e);
    }

    @Override
    public void setUpViews() {
        if (BOARD_SIZE == 6){
            setContentView(R.layout.activity_6x6sudoku);
        }else if(BOARD_SIZE == 9){
            setContentView(R.layout.activity_9x9sudoku);
            setUpViews9();
        }else if(BOARD_SIZE == 12){
            setContentView(R.layout.activity_12x12sudoku);
            setUpViews9();
            setUpViews12();
        }

        gameBoard = findViewById(R.id.sudokuGrid);
        TextView text = findViewById(R.id.Text);
        findViewById(R.id.buttonNullValue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(0);
            }
        });
        findViewById(R.id.button1Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(1);
            }
        });
        findViewById(R.id.button2Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(2);
            }
        });
        findViewById(R.id.button3Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(3);
            }
        });
        findViewById(R.id.button4Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(4);
            }
        });
        findViewById(R.id.button5Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(5);
            }
        });
        findViewById(R.id.button6Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(6);
            }
        });
        findViewById(R.id.buttonCheckBoard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.selectionSound();
                if (check()) {
                    app.winnerSound();
                    Toast.makeText(getApplicationContext(), "Zrobiłeś to! Gratulacje!", Toast.LENGTH_SHORT).show();
                    app.selectedStage.setComplete(true);
                } else {
                    app.notGoodSound();
                    Toast.makeText(getApplicationContext(), "Nie... Nie poprawne.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.buttonMarkValue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.selectionSound();
                if (positionInFocus != -1) {
                    boolean isMarked = content[positionInFocus].isHighlighted();
                    content[positionInFocus].setHighlighted(!isMarked);
                    ((ArrayAdapter) gameBoard.getAdapter()).notifyDataSetChanged();
                }
            }
        });
        text.setText(app.selectedStage.getName());
        gameBoard.setNumColumns(BOARD_SIZE);
        gameBoard.setAdapter(new SudokuGridAdapter(this, R.layout.grid_cell_layout, content));
        gameBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                app.selectionSound();
                if (positionInFocus != -1) {
                    content[lastPositionInFocus].setSelected(false);
                }
                positionInFocus = position;
                content[positionInFocus].setSelected(true);
                lastPositionInFocus = positionInFocus;
                ((ArrayAdapter) gameBoard.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean check() {
        boolean solved = true;

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (traverseHorizontal(i) || traverseVertical(i) || traverseSquareGroups(i)) {
                solved = false;
                break;
            }
        }
        return solved;
    }

    private void setUpViews9() {
        findViewById(R.id.button7Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(7);
            }
        });

        findViewById(R.id.button8Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(8);
            }
        });

        findViewById(R.id.button9Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(9);
            }
        });
    }

    private void setUpViews12() {
        findViewById(R.id.button10Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(10);
            }
        });
        findViewById(R.id.button11Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(11);
            }
        });
        findViewById(R.id.button12Value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(12);
            }
        });
    }

    private boolean traverseSquareGroups(int gp) {
        Set<SudokuCell> gElements = new HashSet<>();
        boolean isEven = gp % 2 == 0;
        for (int i = 0; i < BOARD_SIZE; i++){
            gElements.add(content[group.get(gp).get(i)]);
        }
        return gElements.contains(new SudokuCell(0, false, isEven)) || gElements.size() != BOARD_SIZE;
    }

    private boolean traverseVertical(int column) {
        Set<SudokuCell> vElements = new HashSet<>();
        for (int i = column; i < content.length; i += BOARD_SIZE) {
            vElements.add(content[i]);
        }
        return vElements.contains(new SudokuCell(0, false, false)) || vElements.contains(new SudokuCell(0, false, true)) || vElements.size() != BOARD_SIZE;
    }

    private boolean traverseHorizontal(int line) {
        int startPoint = line * BOARD_SIZE;
        int endPoint = startPoint + (BOARD_SIZE);
        Set<SudokuCell> hElements = new HashSet<>(Arrays.asList(content).subList(startPoint, endPoint));

        return hElements.contains(new SudokuCell(0, false, false)) || hElements.contains(new SudokuCell(0, false, true)) || hElements.size() != BOARD_SIZE;
    }

    private void setValueInSelectedView(int value) {
        app.writingSound();
        if (positionInFocus != -1) {
            content[positionInFocus].setValue(value);
            ((ArrayAdapter) gameBoard.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void toPuzzleString() {
        StringBuilder bufSave = new StringBuilder();
        StringBuilder bufMark = new StringBuilder();
        for (SudokuCell cell : content) {
            if (!cell.isInitialValue()) {
                if (cell.isHighlighted()) {
                    bufMark.append(cell.getValue());
                    bufSave.append(0);
                } else {
                    bufSave.append(cell.getValue());
                    bufMark.append(0);
                }
            }else{
                bufMark.append(0);
                bufSave.append(0);
            }
        }
        app.selectedStage.setExtra(bufMark.toString());
        app.selectedStage.setSave(bufSave.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        toPuzzleString();
        app.saveStage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        toPuzzleString();
        app.saveStage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toPuzzleString();
        app.saveStage();
    }
}
