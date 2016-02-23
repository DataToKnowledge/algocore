import pandas as pd
import numpy as np
import json
from wtl.es.QueryTermsIndexer import *

crimes_path = "../src/main/resources/crimesList.csv"
geofoss_path = "../src/main/resources/headed_gfossdata.csv"

crimes_df = pd.read_csv(crimes_path)
geo_df = pd.read_csv(geofoss_path, sep="|")

crimes = crimes_df[crimes_df.type == 'crime']
unique_provs = pd.Series(greater15k.province_name.unique())
provs = list(cartesian(crimes.name, unique_provs))
