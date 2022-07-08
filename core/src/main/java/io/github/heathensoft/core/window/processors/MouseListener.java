package io.github.heathensoft.core.window.processors;

/**
 * @author Frederik Dahl
 * 01/11/2021
 */


public interface MouseListener {
    
    
    void position(float viewportX, float viewportY, float ndcX, float ndcY);
    
    
    void hover(float viewportX, float viewportY, float deltaX, float deltaY, float ndcX, float ndcY);
    
    
    void click(int button, float viewportX, float viewportY, float ndcX, float ndcY);
    
    
    void scroll(int value, float viewportX, float viewportY, float ndcX, float ndcY);
    
    
    void dragging(int button, float dragVectorX, float dragVectorY, float deltaX, float deltaY);
    
    
    void dragStart(int button, float viewportX, float viewportY, float ndcX, float ndcY);
    
    
    void dragRelease(int button, float viewportX, float viewportY, float ndcX, float ndcY);
    
    
    void onActiveMouseListener();
    
    /** Called when this listener is replaced as the current MouseListener */
    void onDeactiveMouseListener();
    
    
}
