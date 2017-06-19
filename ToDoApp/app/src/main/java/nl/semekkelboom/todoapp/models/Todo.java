package nl.semekkelboom.todoapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by semek on 13-6-2017.
 */

public class Todo {
    private String _id;
    private String text;
    private String _creator;
    private boolean completed;
    private long completedAt;

    public Todo(String _id, String text, String _creator, boolean completed, long completedAt) {
        this._id = _id;
        this.text = text;
        this._creator = _creator;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    public Todo(String _id, String text, String _creator, boolean completed) {
        this._id = _id;
        this.text = text;
        this._creator = _creator;
        this.completed = completed;
        this.completedAt = 0;
    }

    @Override
    public String toString() {
        String output;
        SimpleDateFormat formatter;
        Locale locale = new Locale("nl_NL");

        formatter = new SimpleDateFormat("EEE d MMM yyyy 'at' H:mm", locale);
        if (this.completed) {
            Date d = new Date();
            d.setTime(this.completedAt);
            output = formatter.format(d);
            return "Todo: " + text + "\nDone: " + output;
        } else {
            return "Todo: " + text;
        }
    }

    public String get_id() {
        return _id;
    }

    public String getText() {
        return text;
    }

    public String get_creator() {
        return _creator;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getCompletedAt() {
        return completedAt;
    }
}