# e-NLP


The e-NLP module performs Named Entity Recognition and Temporal Analysis. Given either a trained model, a dictionary file or a temporal expression model, new input can be analyzed and annotated for entities and additional information (like DBpedia URI) in NIF format.

##Named Entity Recognition
The NER endpoint bundles several different approaches. 
The first one is based on NER models which are trained using previously annotated data to extract certain features. These features are then used to select candidate entities. The second one is a simple dictionary approach. A dictionary is uploaded and in new input, every instance of a (group of) word(s) that can be found in the dictionary is annotated as being an entity. 
The third one is a temporal analyzer module. This is based on a regular expression grammar to detect temporal expressions in input and annotate them in the output NIF.
Which approach is used depends on the analysis parameter (described below) and the model specified.
The system is setup so that the output of one component can directly be used as input for the next component, since NIF is the interchange format that is used throughout the whole project.

### Endpoint

http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition

### Input
The API conforms to the general NIF API specifications. For more details, see: http://persistence.uni-leipzig.org/nlp2rdf/specification/api.html

In addition to the input, informat and outformat parameters, the following parameters have to be set to perform Named Entity Recognition on the input:  
  
`language`: The language of the input text. For now, only German (`de`) and English (`en`) are supported.  
  
`analysis`: The type of analysis to perform. Specify `ner` for performing NER based on a trained model. Specify `dict` to perform NER based on an uploaded dictionary. Specify `temp` to perform NER for temporal expressions.  
  
`models`: Specify the name of the models to be used for performing the analysis. Make sure that this model is available (see listModels endpoint) and that you select the right combination of language and model. When using multiple models, they should be separated by a semi-colon (`;`)
  

`mode`: Works for the `ner` analysis only. Possible values are `spot` (for entity spotting only), `link` (for entity linking only, e.g. looking up the entity label on DBPedia to retrieve a URI) or `all` (for both). Default is `all`.



### Output
A document in NIF format annotated with entities depending on the type of analysis selected.

Example cURL post for using the `ner` analysis:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition?language=en&analysis=ner&models=ner-wikinerEn_PER;ner-wikinerEn_LOC&informat=text&outformat=turtle&mode=all&input=Welcome+to+Berlin+in+2016."`

Example of cURL post for using `dict` analysis:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition?language=en&analysis=dict&models=testDummyDict_PER&informat=text&input=wer+weiß,+wo+Herbert+Eulenberg+ging?"`


Example of cURL post for using `temp` analysis:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition?language=en&analysis=temp&models=englishDates&informat=text&outformat=turtle&input=Welcome+to+Berlin+in+2016."`

##List of Models

### Endpoint

http://api.digitale-kuratierung.de/api/e-nlp/listModels

### Input
`analysis`: Specify `ner` to return a list of all available models for the `ner` analysis (for the namedEntityRecognition endpoint), `dict` to return a list of all available models for the `dict` analysis or `temp` to return a list of all available models for the `temp` analysis.

### Output
A list of available models.

Examle cURL post:
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/listModels?analysis=temp"`

## Train Model

### Endpoint

http://api.digitale-kuratierung.de/api/e-nlp/trainModel

### Input
`language`: The language of the input model. (Also here, only German and English are currently supported)

`analysis`: Specify the type of analysis that this model is suitable for. Currently only `dict` and `ner` are supported. For `dict`, a tab-separated file containing two columns is expected, where the first column contains the names that have to be recognized, and the second column contains a URI in some specific ontology. For `ner`, an annotated corpus is expected in the format documented in https://opennlp.apache.org/documentation/1.5.2-incubating/manual/opennlp.html#tools.namefind.training.tool

`modelName`: Specify the desired name of the output model. For `dict`, this should end on _&lt;TYPE&gt; to implicitly communicate the type that any entity recognized with this model should get in the output NIF. E.g. a valid model name is 'Mendelsohn_PER'. For `ner`, there is no restriction on the model name and the type of the entity is inferred from the annotation itself (annotations should look like ```"Former president <START:person> Bill Clinton <END> appeared on stage ."``` or ```"The <START:organization> German Reichstag <END> responded to the claim ."```, where every token should be surrounded by whitespace so that splitting the input on whitespace results in a correctly tokenized text (e.g. tokenization is assumed to be done already)). 

**Note that the model is overwritten every time a new call is made with the same model name and analysis type.**

### Output
A trained model which is stored and can directly be used in the namedEntityRecognition endpoint.

Examle cURL post:
`curl "http://api.digitale-kuratierung.de/api/e-nlp/trainModel?modelName=testModel&language=en&analysis=ner" --data-urlencode "trainingData= <START:person> Pierre Vinken <END> , 61 years old , will join the board as a nonexecutive director Nov. 29 ."`

##Suggesting entity candidates
A random text can be uploaded to this endpoint (in plain text format). This text will be processed and every word in it is referenced to a background corpus, resulting in a TF/IDF value for every term. Depending on this value and the threshold that is used, the term will end up in the output. The output, in list format, will then contain terms that can be considered as descriptive for the uploaded text in particular. The user can manually go through the list, delete irrelevant entries, and use the list as a source for a dictionary that can be uploaded for dictionary-based NER.

### Endpoint

http://api.digitale-kuratierung.de/api/e-nlp/suggestEntityCandidates

### Input
`language`: The language of the input text.

`threshold`: The minimum value that candidates have to have to be shown in the output list. The list of candidates is already sorted by value as it appears in the output (from highest to lowest). The optimal value for the threshold very much depends on the type and amount of input used. Generally, a value of 0.1 is to be advised. But depending on the desired length of the list of suggestions, much smaller values (0.01) can also be useful.

### Output
A list of terms that can be manually checked and then made into a dictionary for dictionary-based NER.

Examle cURL post:
```
curl -X POST -d "Born on 15 October 1844, Nietzsche grew up in the small town of Röcken, near Leipzig, in the Prussian Province of Saxony. He was named after King Frederick William IV of Prussia, who turned forty-nine on the day of Nietzsche'\"'\"'s birth. (Nietzsche later dropped his middle name \"Wilhelm\".[18]) Nietzsche's parents, Carl Ludwig Nietzsche (1813–49), a Lutheran pastor and former teacher, and Franziska Oehler (1826–97), married in 1843, the year before their son's birth. They had two other children: a daughter, Elisabeth Förster-Nietzsche, born in 1846, and a second son, Ludwig Joseph, born in 1848. Nietzsche's father died from a brain ailment in 1849; Ludwig Joseph died six months later, at age two.[19] The family then moved to Naumburg, where they lived with Nietzsche's maternal grandmother and his father's two unmarried sisters. After the death of Nietzsche's grandmother in 1856, the family moved into their own house, now Nietzsche-Haus, a museum and Nietzsche study centre." "http://api.digitale-kuratierung.de/api/e-nlp/suggestEntityCandidates?language=en&threshold=0.2"
```

