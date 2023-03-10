package com.sb.play.bingo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sb.play.adaptor.StatAdaptor;
import com.sb.play.bingo.models.Stat;
import com.sb.play.util.BingoDbUtil;
import com.sb.play.util.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GameStats extends AppCompatActivity {

    private TableLayout overallStatView;
    private TextView totalTextView;
    private TextView wonTextView;
    private TextView lostTextView;
    private TextView winPercentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_stats);

        overallStatView = findViewById(R.id.overallStatView);
        totalTextView = findViewById(R.id.totalTextView);
        wonTextView = findViewById(R.id.wonTextView);
        lostTextView = findViewById(R.id.lostTextView);
        winPercentTextView = findViewById(R.id.winPercentTextView);
        SQLiteDatabase bingoDatabase = BingoDbUtil.getDatabase(this);
        // to update the table structure and keeping the old record.
        BingoDbUtil.importOldStats(this, bingoDatabase);
        try {
            Cursor c = BingoDbUtil.getTotalGamesCursor(bingoDatabase);
            c.moveToNext();
            long total = c.getLong(0);

            c = BingoDbUtil.getWinCountCursor(bingoDatabase);
            c.moveToNext();
            long won = c.getLong(0);

            totalTextView.setText(String.valueOf(total));
            wonTextView.setText(String.valueOf(won));
            lostTextView.setText(String.valueOf(total - won));
            winPercentTextView.setText(Math.round(((double) won / total) * 100) + "%");
        } catch (Exception e) {
            Log.e("Error", "onCreate: Could not calculate the overall stat", e);
            overallStatView.setVisibility(View.GONE);
        }

        List<Stat> statList = new ArrayList<>();
        try {
            Cursor c = BingoDbUtil.getAllGameDetailsCursor(bingoDatabase);
            int roomIndex = c.getColumnIndex(Constants.DbConstants.ROOM_ID_COLUMN);
            int playersIndex = c.getColumnIndex(Constants.DbConstants.PLAYERS_COLUMN);
            int winnerIndex = c.getColumnIndex(Constants.DbConstants.WINNER_COLUMN);
            int timeIndex = c.getColumnIndex(Constants.DbConstants.END_TIME_COLUMN);
            while (c.moveToNext()) {
                statList.add(new Stat(c.getString(roomIndex),
                        c.getString(playersIndex),
                        c.getString(winnerIndex),
                        c.getLong(timeIndex)));
            }
        } catch (Exception e) {
            Log.e("error", "GameStats onCreate: ", e);
        }
        RecyclerView recyclerView = findViewById(R.id.statsLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new StatAdaptor(this, statList));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1,
                GridLayoutManager.VERTICAL, false));
    }

}