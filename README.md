# e-NLP


The e-NLP module performs Named Entity Recognition and Temporal Analysis. Given either a trained model, a dictionary file or a temporal expression model, new input can be analyzed and annotated for entities and additional information (like DBpedia URI) in NIF format.

Named Entity Recognition
The NER endpoint bundles several different approaches. 
The first one is based on NER models which are trained using previously annotated data to extract certain features. These features are then used to select candidate entities. The second one is a simple dictionary approach. A dictionary is uploaded and in new input, every instance of a (group of) word(s) that can be found in the dictionary is annotated as being an entity. 
The third one is a temporal analyzer module. This is based on a regular expression grammar to detect temporal expressions in input and annotate them in the output NIF.
Which approach is used depends on the analysis parameter (described below) and the model specified.
The system is setup so that the output of one component can directly be used as input for the next component, since NIF is the interchange format that is used throughout the whole project.

## Endpoint

http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition

### Input
The API conforms to the general NIF API specifications. For more details, see: http://persistence.uni-leipzig.org/nlp2rdf/specification/api.html
In addition to the input, informat and outformat parameters, the following parameters have to be set to perform Named Entity Recognition on the input:  
  
`language`: The language of the input text. For now, only German (`de`) and English (`en`) are supported.  
  
`analysis`: The type of analysis to perform. Specify `ner` for performing NER based on a trained model. Specify `dict` to perform NER based on an uploaded dictionary. Specify `temp` to perform NER for temporal expressions.  
  
`models`: Specify the name of the models to be used for performing the analysis. Make sure that this model is available (see listModels endpoint), and that you select the right combination of language and model.
  

`mode`: Works for the `ner` analysis only. Possible values are `spot` (for entity spotting only), `link` (for entity linking only, e.g. looking up the entity label on DBPedia to retrieve a URI) or `all` (for both). Default is `all`.



### Output
A document in NIF format annotated with entities depending on the type of analysis selected.

Example cURL post for using the `ner` analysis:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition?language=en&analysis=ner&models=ner-wikinerEn_PER;ner-wikinerEn_LOC&informat=text&outformat=turtle&mode=all&input=Welcome+to+Berlin+in+2016."`

Example of cURL post for using `dict` analysis:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition?language=en&analysis=dict&models=testDummyDict_PER&informat=text&input=wer+weiß,+wo+Herbert+Eulenberg+ging?"`


Example of cURL post for using `temp` analysis:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition?language=en&analysis=temp&models=englishDates&informat=text&outformat=turtle&input=Welcome+to+Berlin+in+2016."`


## Endpoint

http://api.digitale-kuratierung.de/api/e-nlp/listModels

### Input
`analysis`: Specify `ner` to return a list of all available models for the `ner` analysis (for the namedEntityRecognition endpoint), `dict` to return a list of all available models for the `dict` analysis or `temp` to return a list of all available models for the `temp` analysis.

### Output
A list of available models.

Examle cURL post:
`curl -X POST "http://dev.digitale-kuratierung.de/api/e-nlp/listModels?analysis=temp"`

## Endpoint

http://api.digitale-kuratierung.de/api/e-nlp/trainModel

### Input
`language`: The language of the input model.

`analysis`: Specify the type of analysis that this model is suitable for. Currently only `dict` and `ner` are supported. For `dict`, a tab-separated file containing two columns is expected, where the first column contains the names that have to be recognized, and the second column contains a URI in some specific ontology. For `ner`, an annotated corpus is expected in the format documented in https://opennlp.apache.org/documentation/1.5.2-incubating/manual/opennlp.html#tools.namefind.training.tool

`modelName`: Specify the desired name of the output model. For `dict`, this should end on _<TYPE> to implicitly communicate the type that any entity recognized with this model should get in the output NIF. e.g. a valid model name is 'Mendelsohn_PER'. For `ner`, there is no restriction on the model name and the type of the entity is inferred from the annotation itself (annotations should like like "Former president <START:person> Bill Clinton <END> appeared on stage ." or "The <START:organization> German Reichstag <END> responded to the claim .", where every token should be surrounded by whitespace so that splitting the input on whitespace results in a correctly tokenized text (e.g. tokenization is assumed to be done already)). 

**Note that the model is overwritten every time a new call is made with the same model name and analysis type.**

### Output
A trained model which is stored and can directly be used in the namedEntityRecognition endpoint.



