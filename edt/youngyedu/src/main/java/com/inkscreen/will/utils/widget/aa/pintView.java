package com.inkscreen.will.utils.widget.aa;//package will.utils.widget.aa;
//
//import android.app.Activity;
//import android.content.Context;
//import android.util.AttributeSet;
//import android.util.DisplayMetrics;
//import android.view.View;
//
///**
// * Created by xcz on 2016/11/17.
// */
//public class PaintView extends View {
//
//
//    private Canvas mCanvas;
//    private Path mPath;
//    private Paint mBitmapPaint;
//    private Bitmap mBitmap;
//
//    private Paint mPaint;
//
//    private ArrayList<drawpath> savePath;
//
//    private ArrayList<drawpath> deletePath;
//
//    private DrawPath dp;
//
//
//    private float mX, mY;
//
//    private static final float TOUCH_TOLERANCE = 4;
//
//    private int bitmapWidth;
//
//    private int bitmapHeight;
//
//
//    public PaintView(Context c) {
//       super(c);
//        //得到屏幕的分辨率
//       DisplayMetrics dm = new DisplayMetrics();
//      ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//       bitmapWidth = dm.widthPixels;
//       bitmapHeight = dm.heightPixels - 2 * 45;
//
//       initCanvas();
//       savePath = new ArrayList<drawpath>();
//        deletePath = new ArrayList<drawpath>();
//}
//
//
//    public PaintView(Context c, AttributeSet attrs) {
//                    super(c, attrs);
//                    //得到屏幕的分辨率
//                    DisplayMetrics dm = new DisplayMetrics();
//                    ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
//                     
//                    bitmapWidth = dm.widthPixels;
//                    bitmapHeight = dm.heightPixels - 2 * 45;
//                     
//                    initCanvas();
//                    savePath = new ArrayList<drawpath>();
//                    deletePath = new ArrayList<drawpath>();
//                }
//
//            //初始化画布
//                    
//
//    public void initCanvas() {
//                     
//                    mPaint = new Paint();
//                    mPaint.setAntiAlias(true);
//                    mPaint.setDither(true);
//                    mPaint.setColor(0xFF00FF00);
//                    mPaint.setStyle(Paint.Style.STROKE);
//                    mPaint.setStrokeJoin(Paint.Join.ROUND);
//                    mPaint.setStrokeCap(Paint.Cap.ROUND);
//                    mPaint.setStrokeWidth(10); 
//                     
//                    mBitmapPaint = new Paint(Paint.DITHER_FLAG);
//                     
//                    
//                     
//                    //画布大小
//                    mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
//                                Bitmap.Config.RGB_565);
//                    mCanvas = new Canvas(mBitmap);  //所有mCanvas画的东西都被保存在了mBitmap中
//                     
//                    mCanvas.drawColor(Color.WHITE);
//                    mPath = new Path();
//                    mBitmapPaint = new Paint(Paint.DITHER_FLAG);
//                     
//                }
//
//             
//                     
//                    
//    @Override
//            
//
//    protected void onDraw(Canvas canvas) {  
//                     
//                    canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);     //显示旧的画布      
//                    if (mPath != null) {
//                            // 实时的显示
//                            canvas.drawPath(mPath, mPaint);
//                        }
//                }
//
//            //路径对象
//                    
//
//    class DrawPath {
//                    
//        Path path;
//                    
//        Paint paint;
//                
//    }
//
//             
//                    /**
//              * 撤销的核心思想就是将画布清空，
//              * 将保存下来的Path路径最后一个移除掉，
//              * 重新将路径画在画布上面。
//              */
//                    
//
//    public void undo() {
//                     
//                    System.out.println(savePath.size() + "--------------");
//                    if (savePath != null && savePath.size() > 0) {
//                            //调用初始化画布函数以清空画布
//                            initCanvas();
//                             
//                            //将路径保存列表中的最后一个元素删除 ,并将其保存在路径删除列表中
//                            DrawPath drawPath = savePath.get(savePath.size() - 1);
//                            deletePath.add(drawPath);
//                            savePath.remove(savePath.size() - 1);
//                             
//                            //将路径保存列表中的路径重绘在画布上
//                            Iterator<drawpath> iter = savePath.iterator();      //重复保存
//                            while (iter.hasNext()) {
//                                    DrawPath dp = iter.next();
//                                    mCanvas.drawPath(dp.path, dp.paint);
//                                     
//                                }
//                            invalidate();// 刷新
//                        }
//                }
//
//            /**
//              * 恢复的核心思想就是将撤销的路径保存到另外一个列表里面(栈)，
//              * 然后从redo的列表里面取出最顶端对象，
//              * 画在画布上面即可
//              */
//                    
//
//    public void redo() {
//                    if (deletePath.size() > 0) {
//                            //将删除的路径列表中的最后一个，也就是最顶端路径取出（栈）,并加入路径保存列表中
//                            DrawPath dp = deletePath.get(deletePath.size() - 1);
//                            savePath.add(dp);
//                            //将取出的路径重绘在画布上
//                            mCanvas.drawPath(dp.path, dp.paint);
//                            //将该路径从删除的路径列表中去除
//                            deletePath.remove(deletePath.size() - 1);
//                            invalidate();
//                        }
//                }
//
//            /*
//         * 清空的主要思想就是初始化画布
//         * 将保存路径的两个List清空
//         * */
//                    
//
//    public void removeAllPaint() {
//                    //调用初始化画布函数以清空画布
//                    initCanvas();
//                    invalidate();//刷新
//                    savePath.clear();
//                    deletePath.clear();
//                }
//
//             
//                   /*
//        * 保存所绘图形
//        * 返回绘图文件的存储路径
//        * */
//                    
//
//    public String saveBitmap() {
//                    //获得系统当前时间，并以该时间作为文件名
//                    SimpleDateFormat  formatter  =  new  SimpleDateFormat  ("yyyyMMddHHmmss"); 
//                    Date  curDate  =  new  Date(System.currentTimeMillis());//获取当前时间
//                    String  str  =  formatter.format(curDate); 
//                    String paintPath = "";
//                    str = str + "paint.png";
//                    File dir = new File("/sdcard/notes/");
//                    File file = new File("/sdcard/notes/", str);
//                    if (!dir.exists()) {
//                            dir.mkdir();
//                        }
//                    else{
//                            if (file.exists()) {
//                                    file.delete();
//                                }
//                        }
//                     
//                    try {
//                            FileOutputStream out = new FileOutputStream(file);
//                            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                            out.flush();
//                            out.close();
//                            //保存绘图文件路径
//                            paintPath = "/sdcard/notes/" + str;
//                             
//                     
//                        } catch (FileNotFoundException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                     
//                    return paintPath;
//                }
//
//             
//                     
//                    
//
//    private void touch_start(float x, float y) {
//                    mPath.reset();//清空path
//                    mPath.moveTo(x, y);
//                    mX = x;
//                    mY = y;
//                }
//
//            
//
//    private void touch_move(float x, float y) {
//                    float dx = Math.abs(x - mX);
//                    float dy = Math.abs(y - mY);
//                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
//                            //mPath.quadTo(mX, mY, x, y);
//                             
//            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);//源代码是这样写的，可是我没有弄明白，为什么要这样？
//                            mX = x;
//                            mY = y;
//                        }
//                }
//
//            
//
//    private void touch_up() {
//                    mPath.lineTo(mX, mY);
//                    mCanvas.drawPath(mPath, mPaint);
//                    savePath.add(dp);
//                    mPath = null;
//                     
//                }
//
//             
//                    
//    @Override
//            
//
//    public boolean onTouchEvent(MotionEvent event) {
//                    float x = event.getX();
//                    float y = event.getY();
//                     
//                    switch (event.getAction()) {
//                            case MotionEvent.ACTION_DOWN:
//                                     
//                                    mPath = new Path();
//                                    dp = new DrawPath();
//                                    dp.path = mPath;
//                                    dp.paint = mPaint;
//                                     
//                                    touch_start(x, y);
//                                    invalidate(); //清屏
//                                    break;
//                            case MotionEvent.ACTION_MOVE:
//                                    touch_move(x, y);
//                                    invalidate();
//                                    break;
//                            case MotionEvent.ACTION_UP:
//                                    touch_up();
//                                    invalidate();
//                                    break;
//                        }
//                    return true;
//                }
//
//}





//private void postImgBtnLeftOrRightToserver(){
////        linePathView.mBitmap.copy(linePathView.mBitmap.getConfig(), true)
//
//        boolean postTag = false;
//
//        if (textYema.getText().equals("2/2")){
//
//        if(groupPath.size()>1) {
//        for (int i = 0; i < groupPath.size(); i++) {
//        if (i == 0){
//        if ( linePathView.savePath.size() != groupPath.get(i).size()){
//        postTag = linePathView.savePath.size() != groupPath.get(i).size() ? true : false;
//
//        }
//        }
//        }
//        }
//        }else {
//
//        if(groupPath.size()>1) {
//        for (int i = 0; i < groupPath.size(); i++) {
//        if (i == 1){
//        if ( linePathView.savePath.size() != groupPath.get(i).size()){
//        postTag = linePathView.savePath.size() != groupPath.get(i).size() ? true : false;
//
//        }
//        }
//        }
//        }
//
//        }
//
//        if(linePathView.savePath!=null && linePathView.savePath.size()>0  && groupPath.size()<2  || postTag){
//        Toast.makeText(AnswerActivity.this,"bukong",Toast.LENGTH_SHORT).show();
//
//        ImageRequest request = new ImageRequest(LeApiApp.getPostImgUrl(),
//        new Response.Listener < JSONObject > () {
//@Override
//public void onResponse(JSONObject response) {
//
//        try {
//        if(!StringUtils.isEmptyStr(response.getJSONObject("ret").getString("id"))){
//        Log.i("xcz", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@leftorright");
//        // imgList.add(response.getJSONObject("ret").getString("id"));
////
//        if(textYema.getText().equals("2/2")){
//        imgMap.put("1/2", response.getJSONObject("ret").getString("id"));
//
//        if (groupPath.size()>0){
//        groupPath.remove(0);
//        }
//
//
//
//        if (linePathView.savePath!=null && linePathView.savePath.size()>0){
//        List<TuyaView.DrawPath> savePath = new ArrayList<>();
//        savePath.addAll(linePathView.savePath);
//        groupPath.add(0, savePath);
//
//        }
//        linePathView.redo();
//
//        if (groupPath.size()>1){
//
//        for (int i=0;i<groupPath.size();i++){
//        if (i==1){
//        linePathView.initPaht(groupPath.get(1));
//        }
//
//        }
//        }
//
//
//
//
//        }else {
//
//        if (groupPath.size()>1){
//        for (int i=0;i<groupPath.size();i++){
//        if (i==1){
//        groupPath.remove(1);
//        }
//
//        }
//
//        }
//
//
//
//        imgMap.put("2/2", response.getJSONObject("ret").getString("id"));
//
//        if (linePathView.savePath!=null && linePathView.savePath.size()>0){
//        List<TuyaView.DrawPath> savePath = new ArrayList<>();
//        savePath.addAll(linePathView.savePath);
//        if (groupPath.size()>0){
//        groupPath.add(1,savePath);
//        }else {
//        groupPath.add(0,savePath);
//        }
//
//        }
//        //  linePathView.savePath.clear();
//        linePathView.redo();
//
//
//
//        if (groupPath.get(0)!=null && groupPath.get(0).size()>0){
//
//        Iterator iter = imgMap.entrySet().iterator();
//        while (iter.hasNext()) {
//        Map.Entry entry = (Map.Entry) iter.next();
//        if(entry.getKey().equals("1/2")){
//        linePathView.initPaht(groupPath.get(0));
//        }
//        }
//
//        }
//
//        Log.i("xcz", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2");
//        }
//
//        }
//        } catch (JSONException e) {
//        e.printStackTrace();
//        }
//        }
//        }, new Response.ErrorListener() {
//@Override
//public void onErrorResponse(VolleyError error) {
//        }
//        },linePathView.mBitmap , "image", "image.jpg", "image/jpeg");
//        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
//        RequestManager.getInstance().addToRequestQueue(request, AnswerActivity.this);
//
//
//
//
//
//
//
//        }else {
//
//        if(textYema.getText().equals("1/2")) {
//        linePathView.redo();
//        if(groupPath.size()>0){
//
//        for (int i=0;i<groupPath.size();i++){
//        if (i==0){
//        linePathView.initPaht(groupPath.get(i));
//        Log.i("xcz", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>4");
//        }
//
//        }
//        }
//
//        Log.i("xcz", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>3");
//
//        }else {
//        linePathView.redo();
//        if(groupPath.size()>1){
//        for (int i=0;i<groupPath.size();i++){
//        if (i==1){
//        linePathView.initPaht(groupPath.get(i));
//        Log.i("xcz", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>4");
//        }
//
//        }
//        }else {
//        Iterator iter = imgMap.entrySet().iterator();
//        while (iter.hasNext()) {
//        Map.Entry entry = (Map.Entry) iter.next();
//        if(entry.getKey().equals("2/2")){
//        linePathView.initPaht(groupPath.get(0));
//
//        }
//        }
//
//        }
//
//        }
//        }
//
//
//        }
