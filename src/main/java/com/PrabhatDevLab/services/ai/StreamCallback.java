package com.PrabhatDevLab.services.ai;

public interface StreamCallback {
    void onToken(String token);     // Called every time AI streams a chunk
    void onComplete();              // Called when AI finishes
    void onError(Exception e);      // Called if something goes wrong
}
