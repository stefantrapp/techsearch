package de.fernunihagen.techsearch.restservice.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.fernunihagen.techsearch.data.Language;
import de.fernunihagen.techsearch.restservice.dtos.SearchDto;

@Service()
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Sent2VecServiceImpl implements Sent2VecService {

	private Map<Language, Sent2VecWrapper> wrappers = new HashMap<Language, Sent2VecWrapper>();
	
	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	private Sent2VecConfig sent2VecConfig;

	public Sent2VecServiceImpl(Sent2VecConfig sent2VecConfig) {
		this.sent2VecConfig = sent2VecConfig;
	}
	
	@Override
	public List<Sent2VecResult> search(SearchDto query) {
		Sent2VecWrapper wrapper = null;
		var language = query.getLanguage();
		if (!wrappers.containsKey(language)) {
			wrapper = new Sent2VecWrapper(sent2VecConfig, query.getLanguage());
			beanFactory.autowireBean(wrapper);
			wrappers.put(query.getLanguage(), wrapper);
		} else {
		    wrapper = wrappers.get(language);
		}
		
		var queryResult = wrapper.search(query.getSearchTerm());
		var lines = queryResult.split("\n");

		List<Sent2VecResult> results = new ArrayList<>();
		
		for (String line : lines) {
			if ("".equals(line)) {
				/* Das ist die vorletzte Zeile. Danach kommt nur noch die Aufforderung für die nächste Anfrage */
				break;
			}
			
			int firstSpace = line.indexOf(" ");
			
			if (firstSpace != -1) {
				var similarity = line.substring(0, firstSpace);
				
				var secondSpace = line.indexOf(" ", firstSpace + 1);
				
				if (secondSpace != -1) {
					var lineNumer = line.substring(firstSpace + 1, secondSpace);
					var sentence = line.substring(secondSpace + 1);
					
					var result = new Sent2VecResult();
					result.setSimilarity(Float.parseFloat(similarity));
					result.setLineNumber(Integer.parseInt(lineNumer));
					result.setSentence(sentence);
					results.add(result);
				}
			}
		}
		
		return results;
	}

    @Override
    public void quit() {
        wrappers.values().forEach(w -> w.quit());
        
    }
    
}
