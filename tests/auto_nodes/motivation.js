exec = require('child_process').exec;
function promisify(fn) {
  return function (arg) {
   return new Promise(function executor(resolve, reject){
      fn(arg, function cb(err, res){
        if (err != null) return reject(err);
        resolve(res);
      });
   });
  };
}
function anotherAlg(options){ // a compression algorithm being time-consuming for static analyzers.
  return Promise.resolve(res);
}
function compress(options){
  var _exec = promisify(exec); // workaround
  switch (options.alg) {
    case 'foo':
       return anotherAlg(options);
       break;
    case 'xz':
       if (typeof options.level === 'number') {
        var command = ['xz', "-${options.level}", '--stdout', '-k', options.path].join(' ');
        var compressed = _exec(command);
        return compressed;
       }
  }
}
module.exports = function Util(){};
module.exports.prototype.compress = compress;