/*
 * Copyright 2002,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jexl.parser;

import org.apache.commons.jexl.util.Coercion;
import org.apache.commons.jexl.JexlContext;

/**
 *  || and 'or'
 *
 *  @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 *  @version $Id: ASTOrNode.java,v 1.3 2004/02/28 13:45:20 yoavs Exp $
 */
public class ASTOrNode extends SimpleNode
{
    public ASTOrNode(int id)
    {
        super(id);
    }

    public ASTOrNode(Parser p, int id)
    {
        super(p, id);
    }


    /** Accept the visitor. **/
    public Object jjtAccept(ParserVisitor visitor, Object data)
    {
        return visitor.visit(this, data);
    }

    public Object value(JexlContext jc)
        throws Exception
    {
        Object left = ((SimpleNode) jjtGetChild(0)).value(jc);
        Object right = ((SimpleNode) jjtGetChild(1)).value(jc);

        /*
         * coercion rules
         */

        return (Coercion.coerceBoolean(left).booleanValue()
                || Coercion.coerceBoolean(right).booleanValue()) ?
            Boolean.TRUE : Boolean.FALSE;
    }
}
