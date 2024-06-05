import * as fs from "fs"
const dict={"Main":"hand","Clé":"wrench","Marteau":"hammer","Pied de Biche":"crowbar","Hache":"axe"}
fs.readdirSync("csv").forEach(file=>{
    fs.mkdirSync("loot_tables/lootable",{recursive:true})
    if(file=="blocs.csv")
    {
        parseBlocks(fs.readFileSync("csv/blocs.csv").toString());
    }
    else
    {
        parseTable(fs.readFileSync("csv/"+file).toString(),file);
    }
})
function parseTable(text,file)
{
    const ob=parse(text);
    var res={"bonus_rolls": 0.0,entries:[]}
    for(var k in ob)
    {
        const o=ob[k];
        if(o["rolls"] && o['rolls'].length>0)
        {
            var sp=splitNb(o['rolls']);
            res.rolls={ "type": "minecraft:uniform","max": sp[0],"min": sp[1]}
        }
        var cat=o["categorie"] && o["categorie"].length>1;
        if(o["item"] && o["item"].length>1)
        {
            var item=o["item"];
            if(cat)   
            {
                item="green_resurgence:material_"+o["categorie"]+"_"+item;
            }
            const qts=splitNb(o["quantitée"])
            res.entries.push({ "type": "minecraft:item","functions": [
                {
                  "add": false,
                  "count": {
                    "type": "minecraft:uniform",
                    "max": qts[1],
                    "min": qts[0]
                  },
                  "function": "minecraft:set_count"
                }
              ],
              "name": item,
              "weight": parseFloat(o["pourcentage"])})
        }
        else
            console.log("missing item line :"+k)
          
    }
    res={"type": "minecraft:advancement_location",
    "pools": [res]};
    fs.writeFileSync("loot_tables/lootable/"+file.replace(".csv",".json"),JSON.stringify(res,undefined,3))
    
}
/**
 * 
 * @param {string} text 
 */
function splitNb(text)
{
    var s=text.split("-");
    return [parseFloat(s[0]),parseFloat(s[1]||s[0])]
}
function parseBlocks(text)
{
const ob=parse(text);

var result=[];
for(var k in ob)
{
    const o=ob[k];
    
    if(o["Bloc"])
    {
        var d={block:o["Bloc"],empty:o["Bloc Vide"]?o["Bloc Vide"]:"minecraft:air",tables:{}};
        for(const k1 in o)
        {
            if(k1.startsWith("Table "))
            {
                var tool=k1.replace("Table ","");
                if(dict[tool] && o[k1].length>2 )
                {
                 d.tables[dict[tool]]="green_resurgence:lootable/"+o[k1].trim();
                }
                    
            }
        }
        result.push(d);
    }
    else
    console.log("Missing Block cell for line "+k)
    
    
}
fs.writeFileSync("lootables.json",JSON.stringify(result,undefined,3))
}





/**
 * 
 * @param {string} text 
 */
function parse(text)
{
    var res=[]
    var lines=text.split("\n")
    const heads=lines[0].split(",");
    for(var i=1;i<lines.length;i++)
    {
        var l=lines[i].split(",");
        var ob={}
        for(var i1=0;i1<l.length;i1++)
        {
            if(heads[i1])
                ob[heads[i1].trim()]=l[i1].replace("$virg",",").trim();
            
            
        }
        res.push(ob);
    }
    return res;
}