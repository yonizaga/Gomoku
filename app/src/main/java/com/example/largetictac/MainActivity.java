package com.example.largetictac;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Model model = new Model();
    boolean gameWon = false;
    private final ArrayList<Integer> IDS = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        LinearLayout llMainDynamic = findViewById(R.id.llDynamic);
        llMainDynamic.setOrientation(LinearLayout.VERTICAL);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        LinearLayout linearLayoutBoard = new LinearLayout(this);
        linearLayoutBoard.setOrientation(LinearLayout.VERTICAL);
        linearLayoutBoard.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams rowLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLayout.setMargins(width / 17, 1, width / 17, 1);

        LinearLayout.LayoutParams elementLayout = new LinearLayout.LayoutParams(width / 17, width / 17);
        LinearLayout rowInBoard;

        for (int i = 97; i < 112; i++) { // rows 'a' to 'p'
            rowInBoard = new LinearLayout(this);
            rowInBoard.setLayoutParams(rowLayout);
            rowInBoard.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 97; j < 112; j++) { // cols 'a' to 'p'
                Button b = new Button(this);
                b.setLayoutParams(elementLayout);
                b.setTag((char) i + "" + (char) j);
                b.setText((char) i + "" + (char) j);
                b.setOnClickListener(onCellClick());
                b.setId(View.generateViewId());
                b.setBackgroundResource(R.drawable.square);
                IDS.add(b.getId());
                rowInBoard.addView(b);
            }
            linearLayoutBoard.addView(rowInBoard);
        }
        llMainDynamic.addView(linearLayoutBoard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private View.OnClickListener onCellClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. If game is already over, do nothing
                if (gameWon) return;

                Button clickedButton = (Button) view;
                String tag = (String) clickedButton.getTag();

                int row = tag.charAt(0) - 'a';
                int col = tag.charAt(1) - 'a';
                TextView winnerText = findViewById(R.id.whoWon);

                // --- HUMAN MOVE ---
                if (model.isLegal(row, col)) {
                    model.makeMove(row, col);
                    clickedButton.setBackgroundResource(R.drawable.xfortick);

                    // Check if Human Won
                    if (model.checkWin(row, col) != Model.EMPTY) {
                        gameWon = true;
                        winnerText.setText("X Won!");

                        // Fill board with X (Visual effect from your code)
                        for (int ID : IDS) {
                            findViewById(ID).setBackgroundResource(R.drawable.xfortick);
                        }
                        return; // Stop here, don't let AI move
                    }

                    // Check Tie
                    if (model.isTie()) {
                        gameWon = true;
                        winnerText.setText("It's a Tie!");
                        return; // Stop here
                    }

                    // Switch to AI
                    model.changePlayer();

                    // --- AI MOVE ---
                    // Note: We use -1 for AI (O) and 1 for Human (X)
                    Move move = model.getHeuristicMove(-1, 1);
                    model.makeMove(move.row, move.col);

                    char AIMoveRow = (char) (move.row + 'a');
                    char AIMoveCol = (char) (move.col + 'a');

                    // Find the button AI picked
                    Button btn = findViewById(android.R.id.content).findViewWithTag(AIMoveRow + "" + AIMoveCol);
                    if (btn != null) {
                        btn.setBackgroundResource(R.drawable.ofortick);
                    }

                    // Check if AI Won
                    if (model.checkWin(move.row, move.col) != Model.EMPTY) {
                        gameWon = true; // FIX: This was missing!
                        winnerText.setText("O Won!");

                        // Fill board with O
                        for (int ID : IDS) {
                            findViewById(ID).setBackgroundResource(R.drawable.ofortick);
                        }
                    } else if (model.isTie()) {
                        gameWon = true; // FIX: Ensure tie stops game
                        winnerText.setText("It's a Tie!");
                    } else {
                        model.changePlayer();
                    }
                }
            }
        };
    }

    public void reset(View view) {
        model.resetGame();
        gameWon = false;
        TextView winnerText = findViewById(R.id.whoWon);
        winnerText.setText("");

        // Reset all buttons to squares
        for (int ID : IDS) {
            Button b = findViewById(ID);
            b.setBackgroundResource(R.drawable.square);
        }
    }
}