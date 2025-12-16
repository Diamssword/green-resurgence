/// <reference types="node" />
const fs = require("fs")
const ph=require("path")
const allBundles = {};
/**
 * @type {{[key:string]:string[]}}
 */
var markers={};
if(fs.existsSync("markers.json"))
    markers=JSON.parse(fs.readFileSync("markers.json","utf-8"))
else
    console.log("no markers.json file detected")
loadStructureDir("structures")
console.log("Adding custom markers")
Object.keys(markers).forEach(key=>{
    const objs=markers[key];
    if(allBundles[key])
        console.warn(key+" already exist in the bundles, the marker will override it")
    allBundles[key]=[];
    objs.forEach(o=>{
        if(allBundles[o])
        {
            allBundles[key]=[...allBundles[key],...allBundles[o]]
        }
        else
            console.warn("marker "+key+" is looking for "+o+" but can't find it.")
    })
})
setupFolder()
function loadStructureDir(mainDir) {
    const bundles = {};
    var subBundles = []
    fs.readdirSync(mainDir).forEach(dir => {
        if (fs.statSync(mainDir + "/" + dir).isDirectory()) {
            const sub = loadStructureDir(mainDir + "/" + dir)
            if (sub.length > 0)
                subBundles=[...subBundles,...sub];
        }
        else if (dir.endsWith(".nbt")) {
            const ind = dir.lastIndexOf("_");
            if (ind > -1) {
                const name = dir.substring(0, ind)
                if (!bundles[name])
                    bundles[name] = [];
                bundles[name].push(mainDir.replace("structures/", "build:") + "/" + dir.replace(".nbt", ""));
            }

        }
    });
    var ret=[];
        for (const ind in bundles) {
            var path = mainDir.replace("structures/", "build:") + "/" + ind;
            if (ind == "main")
                ret=bundles[ind];
            else
            {
                if (!allBundles[path]) {
                    allBundles[path] = [];
                }
                else
                    console.warn("Conflit path " + path)
                bundles[ind].forEach(v1 => {
                    allBundles[path].push(v1)
                })
            }
        }
        if(subBundles.length>0)
        {
            path = mainDir.replace("structures/", "build:");
            var s="".split("/")
            s[s.length-2]
            subBundles.forEach(v=>{
                let sp=v.split("/")
                if(sp.length>=2)
                {
                    let p1=path+"/"+sp[sp.length-2];
                    if (!allBundles[p1]) {
                        allBundles[p1] = [];
                    }
                    allBundles[p1].push(v);
                }
            })
            console.log(path)
            if (!allBundles[path]) {
                allBundles[path] = [];
            }
            subBundles.forEach(v1 => {
                allBundles[path].push(v1)
            })
        }
    return ret;
}

function setupFolder() {
    try {
        if(!fs.existsSync("build/worldgen/template_pool"))
        fs.mkdirSync("build/worldgen/template_pool", { recursive: true })
        if(!fs.existsSync("build/structures"))
            fs.mkdirSync("build/structures", { recursive: true })
    } catch (err) { console.error(err) }
    for (let d in allBundles) {
        const dir = d.replace("build:", "");
        var path = "build/worldgen/template_pool/" + dir.substring(0, dir.lastIndexOf("/"));
        var name = dir;
        if (dir.lastIndexOf("/") == -1)
            path = "build/worldgen/template_pool/"
        else {
            if (!fs.existsSync(path))
                fs.mkdirSync(path,{recursive:true})
            name = name.substring(name.lastIndexOf("/"), name.length);
        }
        const json = {
            "name": d,
            "fallback": "minecraft:empty",
            "elements": []
        }
        json.elements = allBundles[d].map(v => {
            return {
                "weight": 1,
                "element": {
                    "location": v,
                    "processors": "minecraft:empty",
                    "projection": "rigid",
                    "element_type": "minecraft:single_pool_element"
                }
            }
        })
        allBundles[d].forEach(v=>{
            let path=v.replace("build:","structures/")+".nbt";
            if(!fs.existsSync(ph.dirname("build/"+path)))
            fs.mkdirSync(ph.dirname("build/"+path),{recursive:true})
            fs.copyFileSync(path,"build/"+path)
        })
        fs.writeFileSync(path + "/" + name + ".json", JSON.stringify(json, null, 3))
        
        
    }

}
