# e-NLP


The e-NLP module performs Named Entity Recognition. Given either a trained model, a dictionary file or a temporal expression model, new input can be analyzed and annotated for entities and additional information (like DBpedia URI) in NIF format.

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
`language`: the language of the input text. For now, only German (`de`) and English (`en`) are supported.  
`analysis`: The type of analysis to perform. Specify `ner` for performing NER based on a trained model. Specify `dict` to perform NER based on an uploaded dictionary. Specify `temp` to perform NER for temporal expressions.  
`models`: Specify the name of the models to be used for performing the analysis. This model has to be trained first. Current list of available models for `ner` analysis:  
`ner-de_aij-wikinerTrain_LOC`  
`ner-de_aij-wikinerTrain_ORG`  
`ner-de_aij-wikinerTrain_PER`  
`ner-wikinerEn_LOC`  
`ner-wikinerEn_ORG`  
`ner-wikinerEn_PER`  
Where the first three models are for German input and the last three for English input.

Current list of available models for `dict` analysis:
`testDummyDict_PER`

Current list of available models for `temp` analysis: 
`germanDates` (if language is de)
`englishDates` (if language is en)

`link`: Specify `no` for this parameter to skip looking up of found entities on DBPedia to retrieve the corresponding DBPedia URI. This lookup part can take a lot of time. 


### Output
A document in NIF format annotated with entities depending on the type of analysis selected.

Example cURL post for using the `ner` analysis:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition?language=en&analysis=ner&models=ner-wikinerEn_PER;ner-wikinerEn_LOC&informat=text&outformat=turtle&input=Welcome+to+Berlin+in+2016."`

Example of cURL post for using `dict` analysis:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition?language=en&analysis=dict&models=testDummyDict_PER&informat=text&input=wer+weiß,+wo+Herbert+Eulenberg+ging?"`


Example of cURL post for using `temp` analysis:  
`curl -X POST "http://api.digitale-kuratierung.de/api/e-nlp/namedEntityRecognition?language=en&analysis=temp&models=englishDates&informat=text&outformat=turtle&input=Welcome+to+Berlin+in+2016."`
