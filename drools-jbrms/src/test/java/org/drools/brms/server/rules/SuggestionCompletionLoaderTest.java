package org.drools.brms.server.rules;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.SessionHelper;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

import junit.framework.TestCase;

public class SuggestionCompletionLoaderTest extends TestCase {

    public void testLoader() throws Exception {
        
        RulesRepository repo = new RulesRepository(SessionHelper.getSession());
        PackageItem item = repo.createPackage( "testLoader", "to test the loader" );
        item.updateHeader( "import java.util.Date" );
        repo.save();
        
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine engine = loader.getSuggestionEngine( item );
        assertNotNull(engine);
        
    }
    
    public void testStripUnNeededFields() {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        String[] result = loader.removeIrrelevantFields( new String[] {"foo", "toString", "class", "hashCode"} );
        assertEquals(1, result.length);
        assertEquals("foo", result[0]);
    }
    
    public void testGetShortNameOfClass() {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        
        assertEquals("Object", loader.getShortNameOfClass( Object.class.getName() ));
        
        assertEquals("Foo", loader.getShortNameOfClass( "Foo" ));
    }
    
    public void testFactTemplates() throws Exception {
        
        RulesRepository repo = new RulesRepository(SessionHelper.getSession());
        PackageItem item = repo.createPackage( "testLoader2", "to test the loader for fact templates" );
        item.updateHeader( "import java.util.Date\ntemplate Person\njava.lang.String name\nDate birthDate\nend" );
        repo.save();
        
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine engine = loader.getSuggestionEngine( item );
        assertNotNull(engine);
        String[] factTypes = engine.getFactTypes();
        
        assertEquals( 2, factTypes.length );
        assertEquals("Date", factTypes[0]);
        assertEquals("Person", factTypes[1]);
        
        String[] fieldsForType = engine.getFieldCompletions( "Person" );
        assertEquals( 2, fieldsForType.length );
        assertEquals("name", fieldsForType[0]);
        assertEquals("birthDate", fieldsForType[1]);
        
        String fieldType = engine.getFieldType( "Person", "name" );
        assertEquals( SuggestionCompletionEngine.TYPE_STRING, fieldType );
        fieldType = engine.getFieldType( "Person", "birthDate" );
        assertEquals( SuggestionCompletionEngine.TYPE_COMPARABLE, fieldType );
    }
    
}
