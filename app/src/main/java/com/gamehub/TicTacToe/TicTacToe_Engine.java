package com.gamehub.TicTacToe;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gamehub.R;

import java.util.Random;

public class TicTacToe_Engine extends View  {

    private Context context;
    private TicTacToeActivity activity;

    private String[][] field;

    private String buttonTag, buttonText, buttonX, buttonY;

    private boolean player1Turn, possibleWin;

    private int roundCount;

    private int player1Points, player2Points, checkForPossibleWinCounter, possibleWinSound;

    private Button possbileWinButton;

    private TextView textViewPlayer1, textViewPlayer2;

    private SoundPool spButton;
    private int tictactoebutton, tictactoebackground, tictactoepossiblewin1, tictactoepossiblewin2,tictactoewin1, tictactoewin2;

    private MediaPlayer mp;

    private Random rdm;

    public TicTacToe_Engine(Context context, TicTacToeActivity activity) {
        super(context);
        this.activity = activity;

        field = new String[3][3];

        this.context = context;

        player1Turn = true;

        roundCount = 0;

        player1Points = 0;
        player2Points = 0;

        possibleWin = false;

        checkForPossibleWinCounter = 0;

        field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            field[i][j] = "";
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributesButton = new AudioAttributes.Builder().
                    setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            spButton = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributesButton)
                    .build();
        }   else {
            spButton = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }


        tictactoebutton = spButton.load(context, R.raw.tictactoebutton, 1);
        tictactoepossiblewin1 = spButton.load(context, R.raw.tictactoepossiblewin1, 1);
        tictactoepossiblewin2 = spButton.load(context, R.raw.tictactoepossiblewin2, 1);
        tictactoewin1 = spButton.load(context, R.raw.tictactoewin1, 1);
        tictactoewin2 = spButton.load(context, R.raw.tictactoewin2, 1);

        mp = MediaPlayer.create(activity, R.raw.tictactoebackground);
        mp.setLooping(true);
        mp.setVolume((float) 0.5, (float) 0.5);
        mp.start();

        rdm = new Random();
    }



    public void buttonClick(View v) {
        if(!possibleWin) {
            spButton.play(tictactoebutton, 1, 1, 0, 0, 1);

            if (!((Button) v).getText().toString().equals("")) return;

            if (player1Turn) {
                ((Button) v).setText("X");
            } else {
                ((Button) v).setText("O");
            }

            roundCount++;

            possibleWin = checkForPossibleWin();
        } else if (possbileWinButton == v) {
            if(possibleWinSound == 0) {
                spButton.play(tictactoewin1, 1, 1,0,0,1);
            } else {
                spButton.play(tictactoewin2, 1, 1, 0, 0, 1);
            }

            if (player1Turn) {
                ((Button) v).setText("X");
                player1Wins();
            } else {
                ((Button) v).setText("O");
                player2Wins();
            }
        } else {
            if(possibleWinSound == 0) {
                spButton.play(tictactoepossiblewin1, 1, 1, 0, 0, 1);
            } else {
                spButton.play(tictactoepossiblewin2, 1, 1, 0, 0, 1);
            }
        }

        if (checkForWin(v)) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn;
        }

    }

    private boolean checkForWin(View v) {

        buttonTag = v.getTag().toString();
        buttonText = ((Button) v).getText().toString();

        buttonX = buttonTag.substring(0,1);
        buttonY = buttonTag.substring(1,2);

        field[Integer.parseInt(buttonX)][Integer.parseInt(buttonY)] = buttonText;

        // Man kann frühestens nach 5 Runden gewinnen, bis dahin Ressourcen schonen
        if(roundCount >= 5) {
            for (int i = 0; i < 3; i++) {
                if (field[i][0].equals(field[i][1])
                        && field[i][0].equals(field[i][2])
                        && !field[i][0].equals("")) {
                    return true;
                }
            }

            for (int i = 0; i < 3; i++) {
                if (field[0][i].equals(field[1][i])
                        && field[0][i].equals(field[2][i])
                        && !field[0][i].equals("")) {
                    return true;
                }
            }

            if (field[0][2].equals(field[1][1])
                    && field[0][2].equals(field[2][0])
                    && !field[0][2].equals("")) {
                return true;
            }

            if (field[0][0].equals(field[1][1])
                    && field[0][0].equals(field[2][2])
                    && !field[0][0].equals("")) {
                return true;
            }
        }
        return false;
    }

    public boolean checkForPossibleWin() {

        if(checkForPossibleWinCounter < 1) {
            possibleWinSound = rdm.nextInt(2);
            String buttonID;
            int resID;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {

                    if ((field[i][j].equals("")
                            && !field[(i + 1) % 3][j].equals("")
                            && field[(i + 2) % 3][j].equals(field[(i + 1) % 3][j]))

                            ||

                            (field[i][j].equals("")
                                    && !field[i][(j + 1) % 3].equals("")
                                    && field[i][(j + 2) % 3].equals(field[i][(j + 1) % 3]))

                            ||

                            (field[i][j].equals("")
                                    && !field[(i + 1) % 3][(j + 1) % 3].equals("")
                                    && field[(i + 2) % 3][(j + 2) % 3].equals(field[(i + 1) % 3][(j + 1) % 3]))
                                    && i == j) {
                        //if((field[i][j].equals("X") && player1Turn)
                        //        || (field[i][j].equals("O")) && !player1Turn) {
                        Log.d("sysout", "checkForPossibleWin: asdasdsa");

                        checkForPossibleWinCounter++;

                        buttonID = "button_" + i + j;
                        resID = activity.getResources().getIdentifier(buttonID, "id", activity.getPackageName());
                        possbileWinButton = activity.findViewById(resID);

                        final Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);

                        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.5, 20);
                        myAnim.setInterpolator(interpolator);

                        possbileWinButton.setBackgroundColor(Color.argb(255, rdm.nextInt(224) + 32, rdm.nextInt(224) + 32, rdm.nextInt(224) + 32));

                        possbileWinButton.startAnimation(myAnim);

                        mp.pause();
                        if (possibleWinSound == 0) {
                            spButton.play(tictactoepossiblewin1, 1, 1, 0, 0, 1);
                            mp.start();
                        } else {
                            spButton.play(tictactoepossiblewin2, 1, 1, 0, 0, 1);
                        }
                        //}
                        return true;
                    }

                }

            }
        }
        return false;
    }

    private void player1Wins() {
        player1Points++;
        Toast.makeText(context,"Player 1 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void player2Wins() {
        player2Points++;
        Toast.makeText(context, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void draw() {
        Toast.makeText(context, "Draw!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePointsText() {
        textViewPlayer1 = activity.findViewById(R.id.text_view_p1);
        textViewPlayer2 = activity.findViewById(R.id.text_view_p2);

        textViewPlayer1.setText("Player 1: " + player1Points);
        textViewPlayer2.setText("Player 2: " + player2Points);


    }

    private void resetBoard() {
        Button button;
        for (int i = 0; i < 3; i++ ) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = "";
                button = (Button) activity.findViewById(getResources().getIdentifier("button_" + i + j,"id" , activity.getPackageName()));
                button.setText("");
                button.setBackgroundColor(0xFFd3d3d3);
            }
        }
        if(!mp.isPlaying()) {
            mp.start();
        }
        roundCount = 0;
        checkForPossibleWinCounter = 0;
        possibleWin = false;
        player1Turn = true;
    }

    public void resetGame() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        resetBoard();
    }

    public void onDestroy() {
        spButton.release();
        spButton = null;

        mp.stop();
        mp.release();
        mp = null;
    }

    public void onPause() {
        mp.pause();
    }

    public void onResume() {
        mp.start();
    }


}
