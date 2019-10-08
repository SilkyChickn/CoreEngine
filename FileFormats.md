# File format specification

MetaModel files are stored as bytes. Here is the specification of the format. 

## Fundamental Data types

#### Float
Size: 4 bytes  
Big Endian

#### Short
Size: 2 bytes  
Big Endian

#### Int
Size: 4 bytes  
Big Endian

#### String
Size: x bytes  
Alias: char[]  
Big Endian

#### Char/Byte
Size: 1 byte  
Sometimes a byte is used as boolean, then 0 is false and all greater than 0 is true.


## MetaModel

### First Sector [MetaData]
MeshCount (int) | Mesh0Size (int) | Mesh1Size (int) | ...

### Second Sector [MeshData]
mMesh0 (MetaMesh) | Mesh1 (MetaMesh) | ...

## MetaMesh

### First Sector [MetaData]
VerticesSize (int) | TextureCoordinatesSize (int) | NormalsSize (int) | TangentsSize (int) | JointIdsSize (int) | WeightsSize (int) | IndicesSize (int) | MaterialSize (int) | CollisionShapeSize (int)

### Second Sector [MeshData]
Vertices (float[]) | TextureCoordinates (float[]) | Normals (float[]) | Tangents (float[]) | JointIds (int[]) | Weights (float[]) | Indices (int[])

### Third Sector [Material]
Material (MetaMaterial)

### Fourth Sector [CollisionShape]
CollisionShape (String)

## MetaMaterial

### First Sector [MetaData]
DefaultDiffuseColor (byte) | DefaultGlowColor (byte) | DiffuseMap size in bytes (short) | NormalMap size in bytes (short) | SpecularMap size in bytes (short) | DisplacementMap size in bytes (short) | AmbientOcclusionMap size in bytes (short) | AlphaMap size in bytes (short) | ReflectionMap size in bytes (short) | GlowMap size in bytes (short) | DefaultDisplacementFactor (byte) | DefaultTiling (byte) | DefaultShininess (byte) | DefaultShineDamping (byte)

### Second Sector [Colors]
DiffuseColor (3 floats) | GlowColor (3 floats)

### Third Sector [Textures]
DiffuseMapPath (String) | NormalMapPath (String) | SpecularMapPath (String) | DisplacementMapPath (String) | AmbientOcclusionMapPath (String) | AlphaMapPath (String) | ReflectionMapPath (String) | GlowMapPath (String)

### Fourth Sector [Floats]
DisplacementFactor (float) | Tiling (float) | Shininess (float) | ShineDamping (float)
