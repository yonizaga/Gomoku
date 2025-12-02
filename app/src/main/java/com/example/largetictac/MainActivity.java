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

        LinearLayout llMainDynamic=findViewById(R.id.llDynamic);
        llMainDynamic.setOrientation(LinearLayout.VERTICAL);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        LinearLayout linearLayoutBoard = new LinearLayout(this);
        linearLayoutBoard.setOrientation(LinearLayout.VERTICAL);
        linearLayoutBoard.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams rowLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLayout.setMargins(width/17,1,width/17,1);
        LinearLayout.LayoutParams elementLayout = new LinearLayout.LayoutParams(width/17,width/17);
        LinearLayout rowInBoard;
        for(int i = 97;i<112;i++){ //rows
            rowInBoard = new LinearLayout(this);
            rowInBoard.setLayoutParams(rowLayout);
            rowInBoard.setOrientation(LinearLayout.HORIZONTAL);
            for(int j = 97;j<112;j++){ //cols
                Button b = new Button(this);
                b.setLayoutParams(elementLayout);
                b.setTag((char)i+""+(char)j);
                b.setText((char)i+""+(char)j);
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
                if(!gameWon){
                Button clickedButton = (Button) view;
                String tag = (String) clickedButton.getTag();

                int row = tag.charAt(0)-'a';
                int col = tag.charAt(1)-'a';
                TextView winnerText = findViewById(R.id.whoWon);
                if(model.isLegal(row,col)){
                    model.makeMove(row,col);
                    clickedButton.setBackgroundResource(R.drawable.xfortick);
                    if(model.checkWin(row,col)!=model.EMPTY){
                        gameWon = true;

                        for (int ID : IDS) {
                            Button b =findViewById(ID);

                            b.setBackgroundResource(R.drawable.xfortick);
                        }
                        winnerText.setText("X");
                    } else if (model.isTie()) {
                        winnerText.setText("No one");

                    }else {
                        model.changePlayer();
                    }
                        if(!gameWon){
                            Move move = model.getHeuristicMove(-1,1);
                            model.makeMove(move.row, move.col);
                            char AIMoveRow = (char) (move.row + 'a');
                            char AIMoveCol = (char) (move.col + 'a');
                            Button btn = (Button) findViewById(android.R.id.content).findViewWithTag(AIMoveRow+""+AIMoveCol);
                            btn.setBackgroundResource(R.drawable.ofortick);
                            if (model.checkWin(move.row, move.col) != model.EMPTY) {

                                for (int ID : IDS) {
                                    Button b = findViewById(ID);

                                    b.setBackgroundResource(R.drawable.ofortick);

                                }
                                winnerText.setText("O");
                            } else if (model.isTie()) {
                                winnerText.setText("No one");

                            } else {
                                model.changePlayer();
                            }
                        }
                    }
                } else {
                    // Illegal move (cell already taken)
                }









            }
        };
    }

    public void reset(View view) {
        model.resetGame();
        gameWon =false;
        TextView winnerText = findViewById(R.id.whoWon);
        winnerText.setText("");
        for (int ID : IDS) {
            Button b =findViewById(ID);

            b.setBackgroundResource(R.drawable.square);
        }

    }
}