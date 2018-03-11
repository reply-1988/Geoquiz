package com.example.jingj;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mVersionTextView;
    private TextView mRestTimesTextView;

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String CHEAT = "cheat_or_not";

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_text, true),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_asia, true),
            new Question(R.string.question_oceans, true)
    };

    private int mCurrentIndex = 0;
    private int mTrueAnswer = 0;
    private boolean mIsCheater;
    private int mCheatTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(CHEAT);
        }

        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
                mTrueButton.setClickable(false);
                mFalseButton.setClickable(false);
            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
                mTrueButton.setClickable(false);
                mFalseButton.setClickable(false);
            }
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });
        mPreviousButton = findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                try {
                    updateQuestion();
                    mTrueButton.setClickable(false);
                    mFalseButton.setClickable(false);
                }catch (ArrayIndexOutOfBoundsException e){
                    Toast.makeText(QuizActivity.this,
                            "该问题已经是第一个问题了", Toast.LENGTH_SHORT).show();
                    mCurrentIndex = mCurrentIndex + 1;
                }
            }
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCheatTimes < 3){
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                    mCheatTimes += 1;
                    mRestTimesTextView.setText("剩余欺骗次数：" + (3 - mCheatTimes));
                }
                else {
                    mCheatButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        updateQuestion();

        mVersionTextView = findViewById(R.id.version_text);
        mVersionTextView.setText("API LEVEL " + Build.VERSION.SDK_INT);

        mRestTimesTextView = findViewById(R.id.rest_times);
        mRestTimesTextView.setText("剩余欺骗次数： " + (3-mCheatTimes));


    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        mTrueButton.setClickable(true);
        mFalseButton.setClickable(true);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if (mIsCheater){
            messageResId = R.string.judgment_toast;
        }else {
            if (answerIsTrue == userPressedTrue) {
                messageResId = R.string.correct_toast;
                mTrueAnswer += 1;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        if (mCurrentIndex + 1 == mQuestionBank.length){
            String grade = String.valueOf(mTrueAnswer/mQuestionBank.length*100) + "%";
            Toast.makeText(this, grade, Toast.LENGTH_SHORT).show();
            mNextButton.setClickable(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT){
            if (data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaverInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(CHEAT, mIsCheater);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
