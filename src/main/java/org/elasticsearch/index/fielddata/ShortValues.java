/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.fielddata;

import org.elasticsearch.ElasticSearchIllegalStateException;
import org.elasticsearch.index.fielddata.util.IntArrayRef;
import org.elasticsearch.index.fielddata.util.LongArrayRef;
import org.elasticsearch.index.fielddata.util.ShortArrayRef;

/**
 */
public interface ShortValues {

    static final ShortValues EMPTY = new Empty();

    /**
     * Is one of the documents in this field data values is multi valued?
     */
    boolean isMultiValued();

    /**
     * Is there a value for this doc?
     */
    boolean hasValue(int docId);

    short getValue(int docId);

    short getValueMissing(int docId, short missingValue);

    ShortArrayRef getValues(int docId);

    Iter getIter(int docId);

    void forEachValueInDoc(int docId, ValueInDocProc proc);

    static interface ValueInDocProc {
        void onValue(int docId, short value);

        void onMissing(int docId);
    }

    static interface Iter {

        boolean hasNext();

        short next();

        static class Empty implements Iter {

            public static final Empty INSTANCE = new Empty();

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public short next() {
                throw new ElasticSearchIllegalStateException();
            }
        }

        static class Single implements Iter {

            public short value;
            public boolean done;

            public Single reset(short value) {
                this.value = value;
                this.done = false;
                return this;
            }

            @Override
            public boolean hasNext() {
                return !done;
            }

            @Override
            public short next() {
                assert !done;
                done = true;
                return value;
            }
        }
    }

    static class Empty implements ShortValues {
        @Override
        public boolean isMultiValued() {
            return false;
        }

        @Override
        public boolean hasValue(int docId) {
            return false;
        }

        @Override
        public short getValue(int docId) {
            throw new ElasticSearchIllegalStateException("Can't retrieve a value from an empty ShortValues");
        }

        @Override
        public short getValueMissing(int docId, short missingValue) {
            return missingValue;
        }

        @Override
        public ShortArrayRef getValues(int docId) {
            return ShortArrayRef.EMPTY;
        }

        @Override
        public Iter getIter(int docId) {
            return Iter.Empty.INSTANCE;
        }

        @Override
        public void forEachValueInDoc(int docId, ValueInDocProc proc) {
            proc.onMissing(docId);
        }
    }

    public static class IntBased implements ShortValues {

        private final IntValues values;

        private final ShortArrayRef arrayScratch = new ShortArrayRef(new short[1], 1);
        private final ValueIter iter = new ValueIter();
        private final Proc proc = new Proc();

        public IntBased(IntValues values) {
            this.values = values;
        }

        @Override
        public boolean isMultiValued() {
            return values.isMultiValued();
        }

        @Override
        public boolean hasValue(int docId) {
            return values.hasValue(docId);
        }

        @Override
        public short getValue(int docId) {
            return (short) values.getValue(docId);
        }

        @Override
        public short getValueMissing(int docId, short missingValue) {
            return (short) values.getValueMissing(docId, missingValue);
        }

        @Override
        public ShortArrayRef getValues(int docId) {
            IntArrayRef arrayRef = values.getValues(docId);
            int size = arrayRef.size();
            if (size == 0) {
                return ShortArrayRef.EMPTY;
            }
            arrayScratch.reset(size);
            for (int i = arrayRef.start; i < arrayRef.end; i++) {
                arrayScratch.values[arrayScratch.end++] = (short) arrayRef.values[i];
            }
            return arrayScratch;
        }

        @Override
        public Iter getIter(int docId) {
            return iter.reset(values.getIter(docId));
        }

        @Override
        public void forEachValueInDoc(int docId, ValueInDocProc proc) {
            values.forEachValueInDoc(docId, this.proc.reset(proc));
        }

        static class ValueIter implements Iter {

            private IntValues.Iter iter;

            public ValueIter reset(IntValues.Iter iter) {
                this.iter = iter;
                return this;
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public short next() {
                return (short) iter.next();
            }
        }

        static class Proc implements IntValues.ValueInDocProc {

            private ValueInDocProc proc;

            public Proc reset(ValueInDocProc proc) {
                this.proc = proc;
                return this;
            }

            @Override
            public void onValue(int docId, int value) {
                proc.onValue(docId, (short) value);
            }

            @Override
            public void onMissing(int docId) {
                proc.onMissing(docId);
            }
        }
    }

    public static class LongBased implements ShortValues {

        private final LongValues values;

        private final ShortArrayRef arrayScratch = new ShortArrayRef(new short[1], 1);
        private final ValueIter iter = new ValueIter();
        private final Proc proc = new Proc();

        public LongBased(LongValues values) {
            this.values = values;
        }

        @Override
        public boolean isMultiValued() {
            return values.isMultiValued();
        }

        @Override
        public boolean hasValue(int docId) {
            return values.hasValue(docId);
        }

        @Override
        public short getValue(int docId) {
            return (short) values.getValue(docId);
        }

        @Override
        public short getValueMissing(int docId, short missingValue) {
            return (short) values.getValueMissing(docId, missingValue);
        }

        @Override
        public ShortArrayRef getValues(int docId) {
            LongArrayRef arrayRef = values.getValues(docId);
            int size = arrayRef.size();
            if (size == 0) {
                return ShortArrayRef.EMPTY;
            }
            arrayScratch.reset(size);
            for (int i = arrayRef.start; i < arrayRef.end; i++) {
                arrayScratch.values[arrayScratch.end++] = (short) arrayRef.values[i];
            }
            return arrayScratch;
        }

        @Override
        public Iter getIter(int docId) {
            return iter.reset(values.getIter(docId));
        }

        @Override
        public void forEachValueInDoc(int docId, ValueInDocProc proc) {
            values.forEachValueInDoc(docId, this.proc.reset(proc));
        }

        static class ValueIter implements Iter {

            private LongValues.Iter iter;

            public ValueIter reset(LongValues.Iter iter) {
                this.iter = iter;
                return this;
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public short next() {
                return (short) iter.next();
            }
        }

        static class Proc implements LongValues.ValueInDocProc {

            private ValueInDocProc proc;

            public Proc reset(ValueInDocProc proc) {
                this.proc = proc;
                return this;
            }

            @Override
            public void onValue(int docId, long value) {
                proc.onValue(docId, (short) value);
            }

            @Override
            public void onMissing(int docId) {
                proc.onMissing(docId);
            }
        }
    }
}
