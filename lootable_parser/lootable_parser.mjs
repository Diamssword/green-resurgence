import * as fs from "fs"
const table_dic={};
parseBlocks(fs.readFileSync("csv/blocks_v2.csv").toString());
/**
 * 
 * @param {string[]} files 
 */
function combineTable(files)
{
    const res={rollMin:1000,rollMax:0,items:{}}
    files.forEach(f=>{
        var ob=parse(fs.readFileSync("csv/"+f+".csv").toString());
        for(var k in ob)
            {
                const o=ob[k];
                if(o["rolls"] && o['rolls'].length>0)
                {
                    var sp=splitNb(o['rolls']);
                    if(sp[0]<res.rollMin)
                        res.rollMin=sp[0];
                    if(sp[1]>res.rollMax)
                        res.rollMax=sp[1];
                }
                var cat=o["categorie"] && o["categorie"].length>1;
                if(o["item"] && o["item"].length>1)
                {
                    var item=o["item"];
                    if(cat)   
                    {
                        item="green_resurgence:material_"+o["categorie"]+"_"+item;
                    }
                    if(!res.items[item])
                    {
                        
                        const qts=splitNb(o["quantitée"])
                        res.items[item]={min:qts[0],max:qts[1],perc:parseFloat(o["pourcentage"])};
                    }
                }
                else
                    console.log("missing item line :"+k+" for table: "+f)
                  
            }
    })
    if(res.rollMin==1000)
        console.log("erreur probable de format pour les rolls de les table "+files)
    return res;
 //   fs.writeFileSync("loot_tables/lootable/"+file.replace(".csv",".json"),JSON.stringify(res,undefined,3))
    
}
/**
 * 
 * @param {{rollMin:number,rollMax:number,items:{[id:string]:{min:number,max:number,perc:number}}}} table 
 * @param {string} name 
 * @returns 
 */
function parseTable(table,name)
{
    var res={"bonus_rolls": 0.0,entries:[],rolls:{"type": "minecraft:uniform","max": table.rollMax,"min": table.rollMin}}
    for(var id in table.items)
    {
        var o=table.items[id];
        res.entries.push({ "type": "minecraft:item","functions": [
            {
              "add": false,
              "count": {
                "type": "minecraft:uniform",
                "max": o.max,
                "min": o.min
              },
              "function": "minecraft:set_count"
            }
          ],
          "name": id,
          "weight": o.perc})
    }
    res={"type": "minecraft:advancement_location",
    "pools": [res]};
    fs.writeFileSync("loot_tables/lootable/"+name+".json",JSON.stringify(res,undefined,3))
    return res;
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
/**
 * 
 * @param {string} id 
 * @param {{replace:string,tables:{[tool:string]:string[]}}} props 
 */
function handleBlock(id,props)
{
    var d={block:id,empty:props.replace,tables:{}};
    for(var tool in props.tables)
    {
        var tn=props.tables[tool].join("_");
        if(!table_dic[tn])
        {
           var t=combineTable(props.tables[tool]);
            parseTable(t,tn)
            table_dic[tn]=t;
        }
        d.tables[tool]="green_resurgence:lootable/"+tn;            
    }
    return d;
    
}
function parseBlocks(text)
{
const ob=parse(text);

var result={};
for(var k in ob)
{
    const o=ob[k];
    var table=o["table"].trim();
    var tool=  o["outils"].trim();
    var replace= o["replace"]?o["replace"]:"minecraft:air"
    if(table && tool && replace)
    {
            o["blocs"].split(" ").forEach(b=>{
                if(!result[b])
                    result[b]={replace, tables:{[tool]:[table]}}
                else
                {
                    if(!result[b].tables[tool])
                        result[b].tables[tool]=[table]
                    else
                        result[b].tables[tool].push(table)
                }
                    
            })
    }
    else
        console.error("Error reading from line "+k);

    
    
}
var res=[]
for(k in result)
{
    res.push(handleBlock(k,result[k]));
}
fs.writeFileSync("lootables.json",JSON.stringify(res,undefined,3))
//fs.writeFileSync("lootables.json",JSON.stringify(result,undefined,3))
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