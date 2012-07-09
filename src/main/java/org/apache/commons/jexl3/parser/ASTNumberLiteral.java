/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jexl3.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public final class ASTNumberLiteral extends JexlNode implements JexlNode.Constant<Number> {
    /** The type literal value. */
    private Number literal = null;
    /** The expected class. */
    private Class<?> clazz = null;

    ASTNumberLiteral(int id) {
        super(id);
    }

    ASTNumberLiteral(Parser p, int id) {
        super(p, id);
    }

    static final DecimalFormat BIGDF = new DecimalFormat("0.0b");

    @Override
    public String toString() {
        if (BigDecimal.class.equals(clazz)) {
              return BIGDF.format(literal);
        } else {
            StringBuilder strb = new StringBuilder(literal.toString());
            if (clazz != null) {
                if (Float.class.equals(clazz)) {
                    strb.append('f');
                } else if (Double.class.equals(clazz)) {
                    strb.append('d');
                } else if (BigDecimal.class.equals(clazz)) {
                    strb.append('b');
                } else if (BigInteger.class.equals(clazz)) {
                    strb.append('h');
                } else if (Long.class.equals(clazz)) {
                    strb.append('l');
                }
            }
            return strb.toString();
        }
    }

    @Override
    public Number getLiteral() {
        return literal;
    }

    @Override
    protected boolean isConstant(boolean literal) {
        return true;
    }

    public Class<?> getLiteralClass() {
        return clazz;
    }

    public boolean isInteger() {
        return Integer.class.equals(clazz);
    }

    /**
     * Sets this node as a natural literal.
     * Originally from OGNL.
     * @param s the natural as string
     */
    void setNatural(String s) {
        Number result;
        Class<?> rclass;
        // determine the base
        final int base;
        if (s.charAt(0) == '0') {
            if ((s.length() > 1 && (s.charAt(1) == 'x' || s.charAt(1) == 'X'))) {
                base = 16;
                s = s.substring(2); // Trim the 0x off the front
            } else {
                base = 8;
            }
        } else {
            base = 10;
        }
        final int last = s.length() - 1;
        switch (s.charAt(last)) {
            case 'l':
            case 'L': {
                rclass = Long.class;
                result = Long.valueOf(s.substring(0, last), base);
                break;
            }
            case 'h':
            case 'H': {
                rclass = BigInteger.class;
                result = new BigInteger(s.substring(0, last), base);
                break;
            }
            default: {
                rclass = Integer.class;
                try {
                    result = Integer.valueOf(s, base);
                } catch (NumberFormatException take2) {
                    try {
                        result = Long.valueOf(s, base);
                    } catch (NumberFormatException take3) {
                        result = new BigInteger(s, base);
                    }
                }
            }
        }
        literal = result;
        clazz = rclass;
    }

    /**
     * Sets this node as a real literal.
     * Originally from OGNL.
     * @param s the real as string
     */
    void setReal(String s) {
        Number result;
        Class<?> rclass;
        if ("#NaN".equals(s)) {
            result = Double.NaN;
            rclass = Double.class;
        } else {
            final int last = s.length() - 1;
            switch (s.charAt(last)) {
                case 'b':
                case 'B': {
                    rclass = BigDecimal.class;
                    result = new BigDecimal(s.substring(0, last));
                    break;
                }
                case 'f':
                case 'F': {
                    rclass = Float.class;
                    result = Float.valueOf(s.substring(0, last));
                    break;
                }
                case 'd':
                case 'D':
                    rclass = Double.class;
                    result = Double.valueOf(s.substring(0, last));
                    break;
                default: {
                    rclass = Double.class;
                    try {
                        result = Double.valueOf(s);
                    } catch (NumberFormatException take3) {
                        result = new BigDecimal(s);
                    }
                    break;
                }
            }
        }
        literal = result;
        clazz = rclass;
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}