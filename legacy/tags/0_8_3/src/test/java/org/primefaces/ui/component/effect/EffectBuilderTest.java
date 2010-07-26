/*
 * Copyright 2009 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.ui.component.effect;

import static org.junit.Assert.*;

import org.junit.Test;

public class EffectBuilderTest {

	@Test
	public void buildHighlightEffectWithNoOptions() {
		String effect = new EffectBuilder("highlight").forComponent("input").build();
		
		assertEquals("new Effect.Highlight(\"input\",{});", effect);
	}
	
	@Test
	public void buildHighlightEffectWithAnOption() {
		String effect = new EffectBuilder("highlight").forComponent("input").withOption("startcolor", "'#FFFFFF'").build();
		
		assertEquals("new Effect.Highlight(\"input\",{startcolor:'#FFFFFF'});", effect);
	}
	
	@Test
	public void buildHighlightEffectWitManyOptions() {
		String effect = new EffectBuilder("highlight").forComponent("input")
													.withOption("startcolor", "'#FFFFFF'")
													.withOption("endcolor", "'#CCCCCC'")
													.withOption("restorecolor", "'#000000'")
													.build();
		
		assertEquals("new Effect.Highlight(\"input\",{startcolor:'#FFFFFF',endcolor:'#CCCCCC',restorecolor:'#000000'});", effect);
	}	
}