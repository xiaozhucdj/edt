package com.inkscreen.model;

import java.util.List;

/**
 * Created by xcz on 2016/12/5.
 */
public class SortInfo {

    int ret_code;
    String ret_msg;

    public Ret ret;

    public static class Ret{

        private String[]  ALL;

        private String OCR;
        private String OTHER;
        private int categoryCode;

        public List<TEXTBOOK> TEXTBOOK ;
        public static class TEXTBOOK{

            boolean seTag;

            public boolean isSeTag() {
                return seTag;
            }

            public void setSeTag(boolean seTag) {
                this.seTag = seTag;
            }

            public TEXTBOOK(int code, String name) {
                this.code = code;
                this.name = name;
            }
            public TEXTBOOK() {

            }

            int categoryCode;
            long code;
            String count;
            String name;
            int phaseCode;
            int sequence;
            int subjectCode;
            public List<Section> sections ;
            public static class Section{
                String allChildren;
                long code;
                String collectCount;
                String fallibleCount;
                String lessonCount;
                int level;
                String name;
                String pcode;
                String questionCount;
                String schoolQCount;
                int textbookCode;
                boolean seTag;

                public boolean isSeTag() {
                    return seTag;
                }

                public void setSeTag(boolean seTag) {
                    this.seTag = seTag;
                }

                public List<Children> children;

                public static class  Children{
                    String allChildren;
                    long code;
                    String collectCount;
                    String fallibleCount;
                    String lessonCount;
                    int level;
                    String name;
                    String pcode;
                    String questionCount;
                    String schoolQCount;
                    int textbookCode;
                    public List<Children1> children;
                    boolean seTag;

                    public boolean isSeTag() {
                        return seTag;
                    }

                    public void setSeTag(boolean seTag) {
                        this.seTag = seTag;
                    }

                    public static class  Children1{

                        String allChildren;
                        long code;
                        String collectCount;
                        String fallibleCount;
                        String lessonCount;
                        int level;
                        String name;
                        String pcode;
                        String questionCount;
                        String schoolQCount;
                        int textbookCode;
                        boolean seTag;

                        public String getAllChildren() {
                            return allChildren;
                        }

                        public void setAllChildren(String allChildren) {
                            this.allChildren = allChildren;
                        }

                        public long getCode() {
                            return code;
                        }

                        public void setCode(long code) {
                            this.code = code;
                        }

                        public String getCollectCount() {
                            return collectCount;
                        }

                        public void setCollectCount(String collectCount) {
                            this.collectCount = collectCount;
                        }

                        public String getFallibleCount() {
                            return fallibleCount;
                        }

                        public void setFallibleCount(String fallibleCount) {
                            this.fallibleCount = fallibleCount;
                        }

                        public String getLessonCount() {
                            return lessonCount;
                        }

                        public void setLessonCount(String lessonCount) {
                            this.lessonCount = lessonCount;
                        }

                        public int getLevel() {
                            return level;
                        }

                        public void setLevel(int level) {
                            this.level = level;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public String getPcode() {
                            return pcode;
                        }

                        public void setPcode(String pcode) {
                            this.pcode = pcode;
                        }

                        public String getQuestionCount() {
                            return questionCount;
                        }

                        public void setQuestionCount(String questionCount) {
                            this.questionCount = questionCount;
                        }

                        public String getSchoolQCount() {
                            return schoolQCount;
                        }

                        public void setSchoolQCount(String schoolQCount) {
                            this.schoolQCount = schoolQCount;
                        }

                        public int getTextbookCode() {
                            return textbookCode;
                        }

                        public void setTextbookCode(int textbookCode) {
                            this.textbookCode = textbookCode;
                        }

                        public boolean isSeTag() {
                            return seTag;
                        }

                        public void setSeTag(boolean seTag) {
                            this.seTag = seTag;
                        }
                    }



                    public String getAllChildren() {
                        return allChildren;
                    }

                    public void setAllChildren(String allChildren) {
                        this.allChildren = allChildren;
                    }

                    public long getCode() {
                        return code;
                    }

                    public void setCode(long code) {
                        this.code = code;
                    }

                    public String getCollectCount() {
                        return collectCount;
                    }

                    public void setCollectCount(String collectCount) {
                        this.collectCount = collectCount;
                    }

                    public String getFallibleCount() {
                        return fallibleCount;
                    }

                    public void setFallibleCount(String fallibleCount) {
                        this.fallibleCount = fallibleCount;
                    }

                    public String getLessonCount() {
                        return lessonCount;
                    }

                    public void setLessonCount(String lessonCount) {
                        this.lessonCount = lessonCount;
                    }

                    public int getLevel() {
                        return level;
                    }

                    public void setLevel(int level) {
                        this.level = level;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getPcode() {
                        return pcode;
                    }

                    public void setPcode(String pcode) {
                        this.pcode = pcode;
                    }

                    public String getQuestionCount() {
                        return questionCount;
                    }

                    public void setQuestionCount(String questionCount) {
                        this.questionCount = questionCount;
                    }

                    public String getSchoolQCount() {
                        return schoolQCount;
                    }

                    public void setSchoolQCount(String schoolQCount) {
                        this.schoolQCount = schoolQCount;
                    }

                    public int getTextbookCode() {
                        return textbookCode;
                    }

                    public void setTextbookCode(int textbookCode) {
                        this.textbookCode = textbookCode;
                    }

                    public List<Children1> getChildren() {
                        return children;
                    }

                    public void setChildren(List<Children1> children) {
                        this.children = children;
                    }
                }


                public String getAllChildren() {
                    return allChildren;
                }

                public void setAllChildren(String allChildren) {
                    this.allChildren = allChildren;
                }

                public void setChildren(List<Children> children) {
                    this.children = children;
                }

                public long getCode() {
                    return code;
                }

                public void setCode(long code) {
                    this.code = code;
                }

                public String getCollectCount() {
                    return collectCount;
                }

                public void setCollectCount(String collectCount) {
                    this.collectCount = collectCount;
                }

                public String getFallibleCount() {
                    return fallibleCount;
                }

                public void setFallibleCount(String fallibleCount) {
                    this.fallibleCount = fallibleCount;
                }

                public String getLessonCount() {
                    return lessonCount;
                }

                public void setLessonCount(String lessonCount) {
                    this.lessonCount = lessonCount;
                }

                public int getLevel() {
                    return level;
                }

                public void setLevel(int level) {
                    this.level = level;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPcode() {
                    return pcode;
                }

                public void setPcode(String pcode) {
                    this.pcode = pcode;
                }

                public String getQuestionCount() {
                    return questionCount;
                }

                public void setQuestionCount(String questionCount) {
                    this.questionCount = questionCount;
                }

                public String getSchoolQCount() {
                    return schoolQCount;
                }

                public void setSchoolQCount(String schoolQCount) {
                    this.schoolQCount = schoolQCount;
                }

                public int getTextbookCode() {
                    return textbookCode;
                }

                public void setTextbookCode(int textbookCode) {
                    this.textbookCode = textbookCode;
                }

                public List<Children> getChildren() {
                    return children;
                }
            }

            public int getCategoryCode() {
                return categoryCode;
            }

            public void setCategoryCode(int categoryCode) {
                this.categoryCode = categoryCode;
            }

            public long getCode() {
                return code;
            }

            public void setCode(long code) {
                this.code = code;
            }

            public String getCount() {
                return count;
            }

            public void setCount(String count) {
                this.count = count;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getPhaseCode() {
                return phaseCode;
            }

            public void setPhaseCode(int phaseCode) {
                this.phaseCode = phaseCode;
            }

            public int getSequence() {
                return sequence;
            }

            public void setSequence(int sequence) {
                this.sequence = sequence;
            }

            public int getSubjectCode() {
                return subjectCode;
            }

            public void setSubjectCode(int subjectCode) {
                this.subjectCode = subjectCode;
            }

            public List<Section> getSections() {
                return sections;
            }

            public void setSections(List<Section> sections) {
                this.sections = sections;
            }
        }

        public String[] getALL() {
            return ALL;
        }

        public void setALL(String[] ALL) {
            this.ALL = ALL;
        }

        public String getOCR() {
            return OCR;
        }

        public void setOCR(String OCR) {
            this.OCR = OCR;
        }

        public String getOTHER() {
            return OTHER;
        }

        public void setOTHER(String OTHER) {
            this.OTHER = OTHER;
        }

        public int getCategoryCode() {
            return categoryCode;
        }

        public void setCategoryCode(int categoryCode) {
            this.categoryCode = categoryCode;
        }

        public List<Ret.TEXTBOOK> getTEXTBOOK() {
            return TEXTBOOK;
        }

        public void setTEXTBOOK(List<Ret.TEXTBOOK> TEXTBOOK) {
            this.TEXTBOOK = TEXTBOOK;
        }
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public Ret getRet() {
        return ret;
    }

    public void setRet(Ret ret) {
        this.ret = ret;
    }
}
