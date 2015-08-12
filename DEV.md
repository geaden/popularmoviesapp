# Dev docs

## Sync cases

 
| Case #   |  Sort Order                |  Sync Type         |  Number of records | Sync?   
|:---------|----------------------------|--------------------|--------------------|:--------
|  1       |  popularity.desc (default) |  Manual (Start)    |   0                |  true         
|  2       |  popularity.desc (default) |  Manual (Finish)   |   20               |  true
|  3       |  vote_avg.desc             |  Manual (Start)    |   0                |  true
|  4       |  vote_avg.desc             |  Manual (Finish)   |   20               |  true
|  5       |  popularity.desc (default) |  Manual (Start)    |   20               |  false
|  6       |  vote_avg.desc             |  Manual (Start)    |   20               |  false
|  7       |  favorites                 |  Manual (Start)    |   0                |  false
|  8       |  popularity.desc (default) |  Periodic (Start)  |   20               |  true
|  9       |  popularity.desc (default) |  Periodic (Finish) |   20               |  true
| 10       |  vote_avg.desc             |  Periodic (Start)  |   20               |  true
| 11       |  vote_avg.desc             |  Periodic (Start)  |   20               |  true
