package com.example.mateusz.lamimozgi;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mateusz.lamimozgi.adapters.GridAdapter;
import com.example.mateusz.lamimozgi.items.SudokuCell;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SudokuActivity extends AppCompatActivity {

    private static final int BOARD_SIZE = 9;
    private GameApplication app;
    private SudokuCell[] content;
    private GridView gameBoard;
    private TextView viewInFocus;
    private int positionInFocus;
    private Drawable res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_9x9sudoku);
        app = (GameApplication) getApplication();
        String board = app.selectedStage.getStage();
        String save = app.selectedStage.getSave();
        String mark = app.selectedStage.getExtra();
        System.out.println(save);
        System.out.println(mark);
        content = fromPuzzleString(board, save, mark);
        setUpViews();
    }

    private SudokuCell[] fromPuzzleString(String string, String save, String mark) {
        SudokuCell[] puz = new SudokuCell[string.length()];
        for (int i = 0; i < puz.length; i++) {
            int value = string.charAt(i) - '0';
            int saveValue = save.charAt(i) - '0';
            int markValue = mark.charAt(i) - '0';

            int modI9 = i%9;
            boolean isEven = modI9<= 2 || modI9>=6;
            if (i>=27&&i<=53){
                isEven=!isEven;
            }
            boolean isHighlighted = false;
            boolean isInitial = true;
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
            SudokuCell sc = new SudokuCell(value, isInitial, isEven);
            sc.setHighlighted(isHighlighted);
            puz[i] = sc;
        }
        return puz;
    }

    private void setUpViews() {
        gameBoard = findViewById(R.id.sudokuGrid);
        Button one = findViewById(R.id.button1Value);
        Button two = findViewById(R.id.button2Value);
        Button three = findViewById(R.id.button3Value);
        Button four = findViewById(R.id.button4Value);
        Button five = findViewById(R.id.button5Value);
        Button six = findViewById(R.id.button6Value);
        Button seven = findViewById(R.id.button7Value);
        Button eight = findViewById(R.id.button8Value);
        Button nine = findViewById(R.id.button9Value);
        Button check = findViewById(R.id.buttonCheckBoard);
        Button mark = findViewById(R.id.buttonMarkValue);
        Button clear = findViewById(R.id.buttonNullValue);
        TextView text = findViewById(R.id.Text);
        text.setText(app.selectedStage.getName());
        ListAdapter adapter = new GridAdapter(this, R.layout.grid_cell_layout, content);
        gameBoard.setAdapter(adapter);
        gameBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (viewInFocus != null) {
                    viewInFocus.setBackground(res);
                }
                viewInFocus = (TextView) view;
                positionInFocus = position;
                res = view.getBackground();
                view.setBackgroundResource(R.drawable.selected_cell_background);
            }
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(1);
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(2);
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(3);
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(4);
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(5);

            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(6);
            }
        });

        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(7);
            }
        });

        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(8);
            }
        });

        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(9);
            }
        });

        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionInFocus != -1) {
                    boolean isMarked = content[positionInFocus].isHighlighted();
                    content[positionInFocus].setHighlighted(!isMarked);
                    ((ArrayAdapter) gameBoard.getAdapter()).notifyDataSetChanged();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValueInSelectedView(0);
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean solved = true;

                for (int i = 0; i < BOARD_SIZE; i++) {
                    if (!traverseHorizontal(i) || !traverseVertical(i) || !traverseSquareGroups(i)) {
                        solved = false;
                        break;
                    }
                }
                if (solved) {
                    Toast.makeText(getApplicationContext(), "You did it! Congrats!",
                            Toast.LENGTH_SHORT).show();
                    app.selectedStage.setComplete(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Nope... Not correct.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void toPuzzleString() {
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

    private boolean traverseSquareGroups(int group) {
        Set<SudokuCell> gElements = new HashSet<>();
        boolean isEven = group % 2 == 0;
        int groupIndex = 3*(6*(group/3)+group);
        System.out.println(groupIndex);

        gElements.add(content[groupIndex]);
        gElements.add(content[groupIndex+1]);
        gElements.add(content[groupIndex+2]);

        gElements.add(content[(groupIndex)+BOARD_SIZE]);
        gElements.add(content[(groupIndex)+BOARD_SIZE+1]);
        gElements.add(content[(groupIndex)+BOARD_SIZE+2]);

        gElements.add(content[(groupIndex)+(BOARD_SIZE*2)]);
        gElements.add(content[(groupIndex)+(BOARD_SIZE*2)+1]);
        gElements.add(content[(groupIndex)+(BOARD_SIZE*2)+2]);

        return !(gElements.contains(new SudokuCell(0, false, isEven)) || gElements.size() != BOARD_SIZE);
    }

    private boolean traverseVertical(int column) {
        Set<SudokuCell> vElements = new HashSet<>();
        for (int i = column; i < content.length; i += BOARD_SIZE) {
            vElements.add(content[i]);
        }
        return !(vElements.contains(new SudokuCell(0, false, false)) || vElements.contains(new SudokuCell(0, false, true)) || vElements.size() != BOARD_SIZE);
}

    private boolean traverseHorizontal(int line) {
        int startPoint = line*BOARD_SIZE;
        int endPoint = startPoint+(BOARD_SIZE);
        Set<SudokuCell> hElements = new HashSet<>(Arrays.asList(content).subList(startPoint, endPoint));

        return !(hElements.contains(new SudokuCell(0, false, false)) || hElements.contains(new SudokuCell(0, false, true)) || hElements.size() != BOARD_SIZE);
    }

    private void setValueInSelectedView(int value) {
        if (positionInFocus != -1) {
            content[positionInFocus].setValue(value);
            ((ArrayAdapter) gameBoard.getAdapter()).notifyDataSetChanged();
        }
    }

    protected void onPause() {
        super.onPause();
        toPuzzleString();
        app.saveStage();
    }

    protected void onStop() {
        super.onStop();
        toPuzzleString();
        app.saveStage();
    }

    protected void onDestroy() {
        super.onDestroy();
        toPuzzleString();
        app.saveStage();
    }
}
