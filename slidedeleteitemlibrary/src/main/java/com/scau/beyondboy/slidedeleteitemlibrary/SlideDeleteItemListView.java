package com.scau.beyondboy.slidedeleteitemlibrary;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
/**
 * <p>
 * expanding listview implements sliding to delete item and implements {@link SlideItemWrapView.onSlideStopCallBack} to excute
 * something when stop sliding.
 * </p>
 */
public class SlideDeleteItemListView extends ListView {
    /**silde from right to left*/
    public static final int DIRECTION_LEFT=1;
    /**silde from left to right*/
    public static final int DIRECTION_RIGHT=-1;
    /**decide direction of slide.set either {@link #DIRECTION_LEFT} or {@link #DIRECTION_RIGHT}*/
    private int mDirection=DIRECTION_LEFT;
    private float mDownX;
    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;
    private int MAX_Y = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private int MAX_X = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private float mDownY;
    private int mTouchPosition=0;
    private int mTouchState=TOUCH_STATE_NONE;
    private SlideItemWrapView mTouchView;
    private onSwipeItemListen mSwipeItemListen;
    private SlideItemWrapView.onSlideStopCallBack mOnSlideStopCallBack;
    /**{@link SlideItemWrapView#isAutoSpringBack}*/
    private boolean isAutoSpringBack =false;
    /**determine whether touch_up event is given to mBackGroudView's child to control whether excutes onclick() method.*/
    private boolean isInterceptEvent=false;
    public onSwipeItemListen getSwipeItemListen() {
        return mSwipeItemListen;
    }

    public void setSwipeItemListen(onSwipeItemListen swipeItemListen) {
        mSwipeItemListen = swipeItemListen;
    }

    public SlideDeleteItemListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int direction) {
        mDirection = direction;
    }

    public SlideDeleteItemListView(Context context) {
        this(context,null);
    }

    /**
     * testing below code ,I find there special two situation to be handled for listview:
     * 1.when users touch non-clickable component in listview's item,it will pass all touch events（such as action_up,action_move,action_up）
     * to current listview's {@link #onTouchEvent(MotionEvent)},until it return false.
     * 2.if users touch clickable component in listview's item,we consider it carefullly whether it {@link #onInterceptHoverEvent(MotionEvent)}
     * returns true.if true,it will pass current touch event to listview's{@link #onTouchEvent(MotionEvent)} and continued touch events no longer
     * are passed to {@link #onInterceptHoverEvent(MotionEvent)},but still to {@link #onTouchEvent(MotionEvent)} except it returns false.if false,it only will pass current
     * event to listview's{@link #onInterceptTouchEvent(MotionEvent)}.
     */
//        @Override
//        public boolean onInterceptTouchEvent(MotionEvent ev) {
//            int action=ev.getAction();
//            switch (action){
//                case MotionEvent.ACTION_DOWN:
//                    Log.i("测试","onInterceptTouchEvent>>>>>action_down");
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    Log.i("测试","onInterceptTouchEvent>>>>>action_move");
//                    break;
//                case MotionEvent.ACTION_UP:
//                    Log.i("测试","onInterceptTouchEvent>>>>>action_up");
//                    break;
//            }
//            return super.onInterceptTouchEvent(ev);
//        }
//
//        @Override
//        public boolean onTouchEvent(MotionEvent ev) {
//            int action=ev.getAction();
//            switch (action){
//                case MotionEvent.ACTION_DOWN:
//                    Log.i("测试","onTouchEvent>>>>>action_down");
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    Log.i("测试","onTouchEvent>>>>>action_move");
//                    break;
//                case MotionEvent.ACTION_UP:
//                    Log.i("测试","onTouchEvent>>>>>action_up");
//                    break;
//            }
//            return super.onTouchEvent(ev);
//        }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mTouchPosition =pointToPosition((int)ev.getX(),(int)ev.getY());
                View view=getChildAt(mTouchPosition-getFirstVisiblePosition());
                //when a item has been opened and user taps another item.
                if (mTouchView != null && mTouchView.isOpen() && view != mTouchView){
                    return true;
                }
                handleTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                handleTouchEvent(ev);
                //following touch event will be given to its own onTouchEvent() method to handle
                if(mTouchState==TOUCH_STATE_X){
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                //when user only taps the item opened.
                if(mTouchState==TOUCH_STATE_NONE&&mTouchView!=null&&mTouchView.isOpen()){
                    //when BackGroudView has opened,if user click the ContentView,return true to avoid its child performing onclick() method
                    if(!inRangeOfView(mTouchView.getBackGroundView(),ev)){
                        handleTouchEvent(ev);
                        return true;
                    }else {
                        handleTouchEvent(ev);
                        return isInterceptEvent;
                    }
                }
                handleTouchEvent(ev);
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

     @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mTouchPosition =pointToPosition((int)ev.getX(),(int)ev.getY());
                View view=getChildAt(mTouchPosition-getFirstVisiblePosition());
                if (mTouchView != null && mTouchView.isOpen() && view != mTouchView){
                    mTouchView.RollBackToClose();
                    mTouchView=null;
                    return false;
                }
                handleTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                handleTouchEvent(ev);
                if(mTouchState==TOUCH_STATE_X){
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mTouchState==TOUCH_STATE_X||(mTouchView!=null&&mTouchView.isOpen())){
                    handleTouchEvent(ev);
                    //avoid triggering listview's  onItemClick(AdapterView<?> parent, View view, int position, long id) method
                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                    return super.onTouchEvent(cancelEvent);
                }
                break;
        }
         return super.onTouchEvent(ev);
     }

    /**
     * call when user is touching the tiem and the item slide states changes,but excluding automatical slide.
     */
    public interface onSwipeItemListen {
        void onBefore(View TouchView, int position, AbsListView listView);
        void onScroll(View scrollView, int position, float dx, AbsListView listView);
        void onEnd(View TouchView, int position, AbsListView listView);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SlideDeleteItemAdapter(adapter));
    }

    private void handleTouchEvent(MotionEvent ev){
        if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null){
            return ;
        }
        int action=ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mDownX=ev.getX();
                mDownY=ev.getY();
                mTouchState=TOUCH_STATE_NONE;
                mTouchPosition =pointToPosition((int)ev.getX(),(int)ev.getY());
                View view=getChildAt(mTouchPosition-getFirstVisiblePosition());
                if(view instanceof SlideItemWrapView){
                    mTouchView=(SlideItemWrapView)view;
                    mTouchView.onSlide(ev);
                    mTouchView.setSwipeDirection(mDirection);
                    mTouchView.setOnSwipeStopCallBack(mOnSlideStopCallBack);
                    mTouchView.setAutoSpringBack(isAutoSpringBack);
                }else {
                    mTouchView=null;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs((ev.getX() - mDownX));
                float dy = Math.abs((ev.getY() - mDownY));
                //mTouchState decides whether touch-item can slide.
                if(mTouchState==TOUCH_STATE_X){
                    mTouchView.onSlide(ev);
                    if(mSwipeItemListen !=null){
                        mSwipeItemListen.onScroll(mTouchView,mTouchPosition,dx,this);
                    }
                }else if(mTouchState==TOUCH_STATE_NONE){
                    if(Math.abs(dy) > MAX_Y){
                        mTouchState = TOUCH_STATE_Y;
                    }
                    else if(Math.abs(dx)>MAX_X){
                        mTouchState=TOUCH_STATE_X;
                        if(mSwipeItemListen !=null){
                            mSwipeItemListen.onBefore(mTouchView,mTouchPosition,this);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mTouchState==TOUCH_STATE_X){
                    mTouchView.onSlide(ev);
                    if(mSwipeItemListen !=null){
                        mSwipeItemListen.onEnd(mTouchView,mTouchPosition,this);
                    }
                }else if(mTouchView.isOpen()){
                    mTouchView.RollBackToClose();
                }
                break;
        }
    }

    public SlideItemWrapView.onSlideStopCallBack getOnSlideStopCallBack() {
        return mOnSlideStopCallBack;
    }

    public void setOnSlideStopCallBack(SlideItemWrapView.onSlideStopCallBack onSlideStopCallBack) {
        this.mOnSlideStopCallBack = onSlideStopCallBack;
    }

    public boolean isAutoSpringBack() {
        return isAutoSpringBack;
    }

    public void setAutoSpringBack(boolean autoSpringBack) {
        isAutoSpringBack = autoSpringBack;
    }

    public boolean isInterceptEvent() {
        return isInterceptEvent;
    }

    public void setInterceptEvent(boolean interceptEvent) {
        isInterceptEvent = interceptEvent;
    }

    /**
     * detect whether a touch event has landed within the view.
     * @return if true,the view contains coordinates occuring touch event.
     */
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        Rect bgRect=new Rect();
        view.getGlobalVisibleRect(bgRect);
        if (!bgRect.contains((int)ev.getRawX(),(int)ev.getRawY())) {
            return false;
        }
        return true;
    }
}
