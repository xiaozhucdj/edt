/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yougy.common.utils;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.text.ParseException;

public class SvgPathParser {

    private static final int TOKEN_ABSOLUTE_COMMAND = 1;
    private static final int TOKEN_RELATIVE_COMMAND = 2;
    // 具体的数值或".""-"
    private static final int TOKEN_VALUE = 3;
    private static final int TOKEN_EOF = 4;

    private int mCurrentToken;
    private PointF mCurrentPoint = new PointF();
    private int mLength;
    private int mIndex;
    // 要解析的path 字符串��
    private String mPathString;

    protected float transformX(float x) {
        return x;
    }

    protected float transformY(float y) {
        return y;
    }

    private XmlPullParser parser;

    private String parseXml(String xml) {
        parser = Xml.newPullParser();
        ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
        String s = null;
        try {
            parser.setInput(bis, "utf-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                if (type == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equals("polygon")) {
                        String display = parser.getAttributeValue("", "display");
                        if (null == display || !display.equals("none")) {

                        }

                    } else if (name.equals("line")) {
                        int x1 = Integer.parseInt(getAttributeValue("x1"));
                        int y1 = Integer.parseInt(getAttributeValue("y1"));
                        int x2 = Integer.parseInt(getAttributeValue("x2"));
                        int y2 = Integer.parseInt(getAttributeValue("y2"));
                        LogUtils.e("Parser", "(x1,y1):" + x1 + "," + y1 + ",(x2,y2):" + x2 + "," + y2);
                        int color = Color.parseColor(getAttributeValue("stroke"));
                        String widthStr = getAttributeValue("stroke-width");
                        int strokeWidth = Integer.parseInt(widthStr.substring(0,widthStr.length()-2));
                        LogUtils.e("Parser","strokeWidth is : " + strokeWidth);
                    } else if (name.equals("ellipse")) {
                        String display = parser.getAttributeValue("", "display");
                        if (null == display || !display.equals("none")) {
                            int cx = Integer.parseInt(getAttributeValue("cx"));
                            int cy = Integer.parseInt(getAttributeValue("cy"));
                            int rx = Integer.parseInt(getAttributeValue("rx"));
                            int ry = Integer.parseInt(getAttributeValue("ry"));
                            LogUtils.e("Parser", "(cx,cy):" + cx + "," + cy + ",(rx,ry):" + rx + "," + ry);
                        }
                    }
                }
                type = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    private String getAttributeValue(String attributeName) {
        return parser.getAttributeValue("", attributeName);
    }

    public Path parsePath(String xml) throws ParseException {
        String s = parseXml(xml);
        if (null == s) {
            return null;
        }
        LogUtils.e("Parser", "s is : " + s);
        mCurrentPoint.set(Float.NaN, Float.NaN);
        mPathString = s;
        mIndex = 0;
        mLength = mPathString.length();

        PointF tempPoint1 = new PointF();
        PointF tempPoint2 = new PointF();
        PointF tempPoint3 = new PointF();

        Path p = new Path();
        p.setFillType(Path.FillType.WINDING);

        boolean firstMove = true;
        while (mIndex < mLength) {
            char command = consumeCommand();
            boolean relative = (mCurrentToken == TOKEN_RELATIVE_COMMAND);
            switch (command) {
                case 'M':
                case 'm': {
                    // m指令，相当于android 里的 moveTo()
                    boolean firstPoint = true;
                    while (advanceToNextToken() == TOKEN_VALUE) {
                        consumeAndTransformPoint(tempPoint1,
                                relative && mCurrentPoint.x != Float.NaN);
                        if (firstPoint) {
                            p.moveTo(tempPoint1.x, tempPoint1.y);
                            firstPoint = false;
                            if (firstMove) {
                                mCurrentPoint.set(tempPoint1);
                                firstMove = false;
                            }
                        } else {
                            p.lineTo(tempPoint1.x, tempPoint1.y);
                        }
                    }
                    mCurrentPoint.set(tempPoint1);
                    break;
                }

                case 'C':
                case 'c': {
                    // c指令，相当于android 里的 cubicTo()
                    if (mCurrentPoint.x == Float.NaN) {
                        throw new ParseException("Relative commands require current point", mIndex);
                    }

                    while (advanceToNextToken() == TOKEN_VALUE) {
                        consumeAndTransformPoint(tempPoint1, relative);
                        consumeAndTransformPoint(tempPoint2, relative);
                        consumeAndTransformPoint(tempPoint3, relative);
                        p.cubicTo(tempPoint1.x, tempPoint1.y, tempPoint2.x, tempPoint2.y,
                                tempPoint3.x, tempPoint3.y);
                    }
                    mCurrentPoint.set(tempPoint3);
                    break;
                }

                case 'L':
                case 'l': {
                    // 相当于lineTo()进行画直线�
                    if (mCurrentPoint.x == Float.NaN) {
                        throw new ParseException("Relative commands require current point", mIndex);
                    }

                    while (advanceToNextToken() == TOKEN_VALUE) {
                        consumeAndTransformPoint(tempPoint1, relative);
                        p.lineTo(tempPoint1.x, tempPoint1.y);
                    }
                    mCurrentPoint.set(tempPoint1);
                    break;
                }

                case 'H':
                case 'h': {
                    // 画水平直线
                    if (mCurrentPoint.x == Float.NaN) {
                        throw new ParseException("Relative commands require current point", mIndex);
                    }

                    while (advanceToNextToken() == TOKEN_VALUE) {
                        float x = transformX(consumeValue());
                        if (relative) {
                            x += mCurrentPoint.x;
                        }
                        p.lineTo(x, mCurrentPoint.y);
                    }
                    mCurrentPoint.set(tempPoint1);
                    break;
                }

                case 'V':
                case 'v': {
                    // 画竖直直线��
                    if (mCurrentPoint.x == Float.NaN) {
                        throw new ParseException("Relative commands require current point", mIndex);
                    }

                    while (advanceToNextToken() == TOKEN_VALUE) {
                        float y = transformY(consumeValue());
                        if (relative) {
                            y += mCurrentPoint.y;
                        }
                        p.lineTo(mCurrentPoint.x, y);
                    }
                    mCurrentPoint.set(tempPoint1);
                    break;
                }

                case 'Z':
                case 'z': {
                    // 封闭path
                    p.close();
                    break;
                }
            }

        }

        return p;
    }

    private int advanceToNextToken() {
        while (mIndex < mLength) {
            char c = mPathString.charAt(mIndex);
            if ('a' <= c && c <= 'z') {
                return (mCurrentToken = TOKEN_RELATIVE_COMMAND);
            } else if ('A' <= c && c <= 'Z') {
                return (mCurrentToken = TOKEN_ABSOLUTE_COMMAND);
            } else if (('0' <= c && c <= '9') || c == '.' || c == '-') {
                LogUtils.e("Parser", "c is : " + c);
                return (mCurrentToken = TOKEN_VALUE);
            }
            ++mIndex;
        }

        return (mCurrentToken = TOKEN_EOF);
    }

    private char consumeCommand() throws ParseException {
        advanceToNextToken();
        if (mCurrentToken != TOKEN_RELATIVE_COMMAND && mCurrentToken != TOKEN_ABSOLUTE_COMMAND) {
            LogUtils.e("Parser", "currentToken is : " + mCurrentToken);
            throw new ParseException("Expected command", mIndex);
        }

        return mPathString.charAt(mIndex++);
    }

    private void consumeAndTransformPoint(PointF out, boolean relative) throws ParseException {
        out.x = transformX(consumeValue());
        out.y = transformY(consumeValue());
        if (relative) {
            out.x += mCurrentPoint.x;
            out.y += mCurrentPoint.y;
        }
    }

    private float consumeValue() throws ParseException {
        advanceToNextToken();
        if (mCurrentToken != TOKEN_VALUE) {
            throw new ParseException("Expected value", mIndex);
        }

        boolean start = true;
        boolean seenDot = false;
        int index = mIndex;
        while (index < mLength) {
            char c = mPathString.charAt(index);
            if (!('0' <= c && c <= '9') && (c != '.' || seenDot) && (c != '-' || !start)) {
                break;
            }
            if (c == '.') {
                seenDot = true;
            }
            start = false;
            ++index;
        }

        if (index == mIndex) {
            throw new ParseException("Expected value", mIndex);
        }

        String str = mPathString.substring(mIndex, index);
        try {
            float value = Float.parseFloat(str);
            mIndex = index;
            return value;
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid float value '" + str + "'.", mIndex);
        }
    }
}
