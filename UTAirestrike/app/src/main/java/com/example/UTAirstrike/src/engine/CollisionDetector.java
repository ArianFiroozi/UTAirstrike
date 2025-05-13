package com.example.UTAirstrike.src.engine;

import com.example.UTAirstrike.src.model.GameObject;

public class ColissionDetector {

    private boolean hasNoHorizontalOverlap(GameObject firstObject, GameObject secondObject){
        float firstObjRightBorder = firstObject.getPosition().getX() + firstObject.getSize().getX()/2;
        float firstObjLeftBorder = firstObject.getPosition().getX() - firstObject.getSize().getX()/2;
        float secondObjRightBorder = secondObject.getPosition().getX() + secondObject.getSize().getX()/2;
        float secondObjLeftBorder = secondObject.getPosition().getX() - secondObject.getSize().getX()/2;

        return (firstObjRightBorder <= secondObjLeftBorder || firstObjLeftBorder >= secondObjRightBorder);
    }

    private boolean hasNoVerticalOverlap(GameObject firstObject, GameObject secondObject){
        float firstObjUpperBorder = firstObject.getPosition().getY() + firstObject.getSize().getY()/2;
        float firstObjLowerBorder = firstObject.getPosition().getY() - firstObject.getSize().getY()/2;
        float secondObjUpperBorder = secondObject.getPosition().getY() + secondObject.getSize().getY()/2;
        float secondObjLowerBorder = secondObject.getPosition().getY() - secondObject.getSize().getY()/2;

        return (firstObjUpperBorder <= secondObjLowerBorder || firstObjLowerBorder >= secondObjUpperBorder);
    }

    public boolean isCollide(GameObject firstObject, GameObject secondObject){
        return !(hasNoHorizontalOverlap(firstObject, secondObject) || hasNoVerticalOverlap(firstObject, secondObject));
    }
}
