# CSV result  - Rest API
**Warning!** this is an experimental feature.<br>
That means that not all cases tested/we are not sure about this feature<br>
but we sure want you to try it and tell us what you think about it
### how to use?
When using the REST api you use something like:<br>
`http://xxxx:9200/_sql?sql=select field from index` <br>
And than you get the result as elasticsearch return the result<br>

Now you can get them as a csv for lighter parsing<br>
Just add the parameter<br>
`http://xxxx:9200/_sql?format=csv&sql=select field from index` <br>
More parameters you can add:
 * flat for nested objects. (just like on site) <br>
`http://xxxx:9200/_sql?flat=true&format=csv&sql=select field from index`
 * change separator (default is ",") <br>
`http://xxxx:9200/_sql?separator=;&format=csv&sql=select field from index`
 * change newLine (default is "\n") <br>
`http://xxxx:9200/_sql?newLine=b&format=csv&sql=select field from index`