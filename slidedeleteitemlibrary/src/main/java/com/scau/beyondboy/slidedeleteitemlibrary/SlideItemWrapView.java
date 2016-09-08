package com.scau.beyondboy.slidedeleteitemlibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import static com.scau.beyondboy.slidedeleteitemlibrary.SlideDeleteItemListView.DIRECTION_LEFT;
import static com.scau.beyondboy.slidedeleteitemlibrary.SlideDeleteItemListView.DIRECTION_RIGHT;
/**
 * <p>
 *     this layout is used to wrap two mContentView and mBackGroundView from listview's item.
 *     you can customize the two view you want.To silde item from both right to left and left to
 *     right,It is needed that {@link AbstractSlideDelItemAdapter} wraps your implemented adapter.
 * </p>
 */
@UiThread
public class SlideItemWrapView extends FrameLayout {
    private static final int CONTENT_VIEW_ID = 1;
    private static final int BACKGROUND_VIEW_ID = 2;
    /**represent left most position of this listview's item*/
    private int mBaseX;
    private View mContentView;
    private View mBackGroundView;
    private Interpolator mCloseInterpolator;
    private int mDownX=0;
    private Interpolator mOpenInterpolator;
    private ScrollerCompat mOpenScroller;
    private ScrollerCompat mCloseScroller;
    /**if true,the item can be silded,otherwise not be silded.by default,the value is true*/
    private boolean mSwipEnable=true;
    /**decide direction of slide.set either {@link SlideDeleteItemListView#DIRECTION_LEFT} or {@link SlideDeleteItemListView#DIRECTION_RIGHT}*/
    private int mSwipeDirection=DIRECTION_LEFT;
    private static final int STATE_ClOSE = 0;
    private static final int STATE_OPEN = 1;
    /**decide whether call {@link onSlideStopCallBack}*/
    private boolean isCallStopCallBack=false;
    private int state = STATE_ClOSE;
    /**store position in all listview's items */
    private int position;
    /**control whether item can be "spring back" to close {@link #mBackGroundView} instantly
     *regardless of slide distance.
     */
    private boolean isAutoSpringBack =false;
    private onSlideStopCallBack mOnSlideStopCallBack;
    public SlideItemWrapView(@NonNull View contentView, @NonNull View backGroundView) {
        this(contentView, backGroundView, null, null);
    }

    public SlideItemWrapView(@NonNull View contentView, @NonNull View backGroundView, Interpolator closeInterpolator, Interpolator openInterpolator) {
        this(contentView.getContext(),null);
        mContentView = contentView;
        mBackGroundView = backGroundView;
        mCloseInterpolator = closeInterpolator;
        mOpenInterpolator = openInterpolator;
        init();
    }

    public SlideItemWrapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private void init(){
        setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (mCloseInterpolator != null) {
            mCloseScroller = ScrollerCompat.create(getContext(),
                    mCloseInterpolator);
        } else {
            mCloseScroller = ScrollerCompat.create(getContext());
        }
        if (mOpenInterpolator != null) {
            mOpenScroller = ScrollerCompat.create(getContext(),
                    mOpenInterpolator);
        } else {
            mOpenScroller = ScrollerCompat.create(getContext());
        }
      /*  LayoutParams contentParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mContentView.setLayoutParams(contentParams);*/
        if (mContentView.getId() < 1) {
            //noinspection ResourceType
            mContentView.setId(CONTENT_VIEW_ID);
        }
        //noinspection ResourceType
        mBackGroundView.setId(BACKGROUND_VIEW_ID);
        addView(mContentView);
        addView(mBackGroundView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mContentView.layout(0,0,getMeasuredWidth(),getMeasuredHeight());
        if(mSwipeDirection==DIRECTION_LEFT){
            mBackGroundView.layout(getMeasuredWidth(),0,getMeasuredWidth()+mBackGroundView.getMeasuredWidth(),getMeasuredHeight());
        }else{
            mBackGroundView.layout(-mBackGroundView.getMeasuredWidth(),0,0,getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //set mBackGroudView's measureWidth and measureHeight
        mBackGroundView.measure(MeasureSpec.makeMeasureSpec(mBackGroundView.getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean onSlide(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                stopScroll();
                mDownX=(int)event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int dis=(int)(mDownX-event.getX());
                //when mBackGroundView has been opened and user slide this item again.
                if(state== STATE_OPEN){
                    dis+=mBackGroundView.getWidth()*mSwipeDirection;
                }
                swipe(dis);
                break;
            case MotionEvent.ACTION_UP:
                int halfWith=0;
                //whether distance of item's slide is beyond its half.
                if(mSwipeDirection==DIRECTION_LEFT){
                    halfWith=mContentView.getWidth()-mBackGroundView.getLeft();
                }else if(mSwipeDirection==DIRECTION_RIGHT){
                    halfWith=mBackGroundView.getWidth()+mBackGroundView.getLeft();
                }
                if(Math.abs(halfWith)>(mBackGroundView.getWidth()>>1)&&!isAutoSpringBack){
                    smoothOpenItem();
                }else{
                    smoothCloseItem();
                    return false;
                }
                break;
        }
        return true;
    }

    public onSlideStopCallBack getOnSwipeStopCallBack() {
        return mOnSlideStopCallBack;
    }

    public void setOnSwipeStopCallBack(onSlideStopCallBack onSlideStopCallBack) {
        mOnSlideStopCallBack = onSlideStopCallBack;
    }

    private void smoothCloseItem() {
        state = STATE_ClOSE;
        isCallStopCallBack=true;
        if (mSwipeDirection == DIRECTION_LEFT) {
            mBaseX = -mContentView.getLeft();
            mCloseScroller.startScroll(0, 0, mBackGroundView.getWidth(), 0, 350);
        } else {
            mBaseX = mBackGroundView.getRight();
            mCloseScroller.startScroll(0, 0, mBackGroundView.getWidth(), 0, 350);
        }
        postInvalidate();
    }

    private void smoothOpenItem() {
        if(!mSwipEnable){
            return;
        }
        state= STATE_OPEN;
        isCallStopCallBack=true;
        if(mSwipeDirection==DIRECTION_LEFT){
            mOpenScroller.startScroll(-mContentView.getLeft(),0,mBackGroundView.getWidth(),0,350);
        }else{
            mOpenScroller.startScroll(mContentView.getLeft(),0,mBackGroundView.getWidth(),0,350);
        }
        postInvalidate();
    }

    public void setSwipEnable(boolean swipEnable){
        mSwipEnable = swipEnable;
    }

    public boolean getSwipEnable(){
        return mSwipEnable;
    }

    public boolean isOpen(){
        return state== STATE_OPEN;
    }

    /**
     *  when ation_move event occurs or {@link #RollBackToClose()},it will be invoked.
     *
     *  @param dis the distance of the slide.
     *
     *  @see #onSlide(MotionEvent)
     */
    private void swipe(int dis){
        if(!mSwipEnable){
            return;
        }
        //whether user is silding the item.
        if(Math.signum(dis)!=mSwipeDirection){
            dis=0;
        }else if(Math.abs(dis)> mBackGroundView.getWidth()){
            dis=mBackGroundView.getWidth()*mSwipeDirection;
        }
        mContentView.layout(-dis, mContentView.getTop(),
                mContentView.getWidth() -dis, getMeasuredHeight());
        if (mSwipeDirection == DIRECTION_LEFT) {
            mBackGroundView.layout(mContentView.getWidth() - dis, mBackGroundView.getTop(),
                    mContentView.getWidth() + mBackGroundView.getWidth() - dis,
                    mBackGroundView.getBottom());
        } else {
            mBackGroundView.layout(-mBackGroundView.getWidth() - dis, mBackGroundView.getTop(),
                    - dis, mBackGroundView.getBottom());
        }
    }

    /**
     * when {@link #smoothCloseItem()} or {@link #smoothOpenItem()} is called,it will be invoked.
     *
     * @see #smoothCloseItem()
     * @see #smoothOpenItem()
     */
    @Override
    public void computeScroll() {
        if (state == STATE_OPEN) {
            if (mOpenScroller.computeScrollOffset()) {
                swipe(mOpenScroller.getCurrX()*mSwipeDirection);
                postInvalidate();
            }else{
                if(mOnSlideStopCallBack !=null&&isCallStopCallBack){
                    isCallStopCallBack=false;
                    mOnSlideStopCallBack.openStop(this,position);
                }
            }
        } else {
            if (mCloseScroller.computeScrollOffset()) {
                swipe((mBaseX - mCloseScroller.getCurrX())*mSwipeDirection);
                postInvalidate();
            }else {
                if(mOnSlideStopCallBack !=null&&isCallStopCallBack){
                    isCallStopCallBack=false;
                    mOnSlideStopCallBack.closeStop(this,position);
                }
            }
        }
    }

    public void setSwipeDirection(int swipeDirection) {
        mSwipeDirection = swipeDirection;
    }

    public void RollBackToClose() {
        mCloseScroller.abortAnimation();
        if (state == STATE_OPEN) {
            state = STATE_ClOSE;
            swipe(0);
        }
    }

    public View getContentView() {
        return mContentView;
    }

    public void setContentView(View contentView) {
        mContentView = contentView;
    }

    public View getBackGroundView() {
        return mBackGroundView;
    }

    public void setBackGroundView(View backGroundView) {
        mBackGroundView = backGroundView;
    }

    /**
     * onSlideStopCallBack interface you can use when the item slides stop in both {@link #state}
     */
    public  interface onSlideStopCallBack {
        /**will be invoked when stop silde for {@link #STATE_OPEN}*/
        void openStop(SlideItemWrapView view, int position);
        /**will be invoked when stop silde for {@link #STATE_ClOSE}*/
        void closeStop(SlideItemWrapView view, int position);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isAutoSpringBack() {
        return isAutoSpringBack;
    }

    public void setAutoSpringBack(boolean autoSpringBack) {
        isAutoSpringBack = autoSpringBack;
    }

    private void stopScroll(){
        mCloseScroller.abortAnimation();
        mOpenScroller.abortAnimation();
    }

}
