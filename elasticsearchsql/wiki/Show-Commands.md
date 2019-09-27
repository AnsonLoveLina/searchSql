## Show Commands
Show commands is just a wrapper for the mapping request.<br>
But using it on the web interface give you information about what are the indices in your cluster, which types they contains and what is the mapping for each type.

### The supported Commands:
1.`Show *` - shows all indices on cluster
 * on `_sql?sql=show *` you'll get all the mapping for all indices  
 * on web interface  you'll get a table of index to types
![image](https://cloud.githubusercontent.com/assets/2933669/10557220/bb8ded4e-74ab-11e5-8b5f-336036513676.png)

2. `Show myIndex` - shows a specific index
 * on `_sql?sql=show myIndex` you'll get the mapping for this specific index  
 * on web interface you'll get a table of type to fields for this specific index
![image](https://cloud.githubusercontent.com/assets/2933669/10557233/25ee33f6-74ac-11e5-8d5d-8a42e13f10d2.png)

3. `Show myIndex/myType` - shows a specific type 
 * on `_sql?sql=show myIndex/myType` you'll get the mapping for this specific type  
 * on web interface you'll get a table of fields to mappings for this specific type
![image](https://cloud.githubusercontent.com/assets/2933669/10557248/ac276082-74ac-11e5-9566-020d0edb4eae.png)