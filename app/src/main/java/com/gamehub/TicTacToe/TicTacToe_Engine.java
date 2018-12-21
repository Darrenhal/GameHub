package com.gamehub.TicTacToe;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
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

    private TextView textViewPlayer1, textViewPlayer2, turn;

    private SoundPool spButton;
    private int tictactoebutton, tictactoepossiblewin1, tictactoepossiblewin2, tictactoewin1, tictactoewin2;

    private MediaPlayer mpbg1, mpbg2;
    private int mpbg1Pos, mpbg2Pos;
    private boolean mpbg1Active;

    private Random rdm;

    String player;

    private boolean wait;

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

        wait = false;

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

        mpbg1 = MediaPlayer.create(activity, R.raw.tictactoebackground1);
        mpbg1.setLooping(true);
        mpbg1.setVolume((float) 0.5, (float) 0.5);
        mpbg1.start();
        mpbg1Active = true;

        mpbg2 = MediaPlayer.create(activity, R.raw.tictactoebackground2);
        mpbg2.setLooping(true);
        mpbg2.setVolume((float) 0.7, (float) 0.7);
        rdm = new Random();
    }



    public void buttonClick(View v) {
        turn = activity.findViewById(R.id.turn);

        if(!possibleWin) {
            spButton.play(tictactoebutton, 1, 1, 0, 0, 1);

            if (!((Button) v).getText().toString().equals("")) return;

            if (player1Turn) {
                ((Button) v).setText("X");
            } else {
                ((Button) v).setText("O");
            }

            roundCount++;

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
            possibleWin = checkForPossibleWin(player1Turn);
            if(possbileWinButton != null && !wait) {
                markPossibleWin();
            }
        }
        if (player1Turn) {
            turn.setText("Player 1's turn");
        } else {
            turn.setText("Player 2's turn");
        }

    }

    private boolean checkForWin(View v) {

        buttonTag = v.getTag().toString();
        buttonText = ((Button) v).getText().toString();

        buttonX = buttonTag.substring(0,1);
        buttonY = buttonTag.substring(1,2);

        field[Integer.parseInt(buttonX)][Integer.parseInt(buttonY)] = buttonText;


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

    public boolean checkForPossibleWin(boolean player1Turn) {
        if(possbileWinButton != null) return true;

        if(player1Turn) {
            player = "X";
        } else {
            player = "O";
        }

        if(checkForPossibleWinCounter < 1) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {

                    if ((field[i][j].equals("")
                            && field[(i + 1) % 3][j].equals(player)
                            && field[(i + 2) % 3][j].equals(player))

                            ||

                            (field[i][j].equals("")
                                    && field[i][(j + 1) % 3].equals(player)
                                    && field[i][(j + 2) % 3].equals(player))

                            ||

                            (field[i][j].equals("")
                                    && field[(i + 1) % 3][(j + 1) % 3].equals(player)
                                    && field[(i + 2) % 3][(j + 2) % 3].equals(player)
                                    && i == j)

                            ||

                            (field[i][j].equals("")
                                    && field[(i + 1) % 3][(j + 2) % 3].equals(player)
                                    && field[(i + 2) % 3][(j + 1) % 3].equals(player)
                                    && i + j == 2)) {

                        checkForPossibleWinCounter++;

                        possibleWinSound = rdm.nextInt(2);
                        String buttonID;
                        int resID;

                        buttonID = "button_" + i + j;
                        resID = activity.getResources().getIdentifier(buttonID, "id", activity.getPackageName());
                        possbileWinButton = activity.findViewById(resID);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void markPossibleWin() {
        final Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);

        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.5, 20);
        myAnim.setInterpolator(interpolator);

        possbileWinButton.setBackgroundColor(Color.argb(255, rdm.nextInt(224) + 32, rdm.nextInt(224) + 32, rdm.nextInt(224) + 32));

        possbileWinButton.startAnimation(myAnim);

        if(mpbg1.isPlaying()) {
            mpbg1Pos = mpbg1.getCurrentPosition();
            mpbg1.pause();
            mpbg1Active = false;
        } else {
            mpbg2Pos = mpbg2.getCurrentPosition();
            mpbg2.pause();
            mpbg1Active = true;
        }
        if (possibleWinSound == 0) {
            spButton.play(tictactoepossiblewin1, 1, 1, 0, 0, 1);
            if(mpbg1Active == true) {
                mpbg2.start();
            } else {
                mpbg1.start();
            }
        } else {
            spButton.play(tictactoepossiblewin2, 1, 1, 0, 0, 1);
            if(mpbg1Active == false) {
                mpbg2.seekTo(mpbg1Pos);
                mpbg2.start();
            } else {
                mpbg1.seekTo(mpbg2Pos);
                mpbg1.start();
            }
        }

        wait = true;
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
                button = activity.findViewById(getResources().getIdentifier("button_" + i + j,"id" , activity.getPackageName()));
                button.setText("");
                button.setBackgroundColor(0xFFd3d3d3);
            }
        }
        if(!mpbg1.isPlaying() && roundCount == 0 && player1Points == 0 && player2Points == 0) {
            mpbg2.pause();
            mpbg1.start();
        }

        roundCount = 0;
        checkForPossibleWinCounter = 0;
        possbileWinButton = null;
        wait = false;
        possibleWin = false;

    }

    public void resetGame() {
        player1Points = 0;
        player2Points = 0;
        player1Turn = true;
        turn = activity.findViewById(R.id.turn);
        turn.setText("Player 1's turn");


        updatePointsText();
        resetBoard();
    }

    public void onDestroy() {
        spButton.release();
        spButton = null;

        mpbg1.stop();
        mpbg1.release();
        mpbg1 = null;

        mpbg2.stop();
        mpbg2.release();
        mpbg2 = null;
    }

    public void onPause() {
        mpbg1.pause();
        mpbg2.pause();
    }

    public void onResume() {
        if(mpbg1Active == true) {
            mpbg1.start();
        } else {
            mpbg2.start();
        }
    }


}
