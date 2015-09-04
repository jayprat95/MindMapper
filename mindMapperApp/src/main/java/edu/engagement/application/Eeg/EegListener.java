package edu.engagement.application.Eeg;

/**
 * Created by alex on 9/3/15.
 */
public interface EegListener {
    void onEegStateChange(EegState state);
    void onEegAttentionReceived(int attention);
}
